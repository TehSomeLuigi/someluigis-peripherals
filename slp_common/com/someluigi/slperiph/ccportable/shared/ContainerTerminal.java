package com.someluigi.slperiph.ccportable.shared;

import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.Vec3;

import com.someluigi.slperiph.CommonProxy;
import com.someluigi.slperiph.SLPMod;
import com.someluigi.slperiph.ccportable.gfx.TerminalRenderer;
import com.someluigi.slperiph.ccportable.shared.PayloadManager.Payload;

public class ContainerTerminal extends Container implements IPacketHandler{

	public static class TerminalTab implements IUpdateListener{
		public static final ItemStack DEFAULT_ICON = new ItemStack( Block.blockSteel );
		
		protected final ContainerTerminal container;
		protected final int index;
		
		protected ItemStack icon = DEFAULT_ICON;
		protected String label = "No Device";
		
		public PeripheralTerminal peripheral;	//Serverside
		public Terminal terminal;				//Clientside
		
		public boolean isLinked		= false;
		public boolean isHilighted	= false;
		
		public TerminalTab( int index, ContainerTerminal container ){
			this.container	= container;
			this.index		= index;
		}
		
		public void setTerminal( PeripheralTerminal newTerminal ){
			if ( peripheral != null )
				peripheral.removeUpdateListener(this);
			
			isLinked = ( newTerminal != null );
			
			if ( isLinked ){
				peripheral = newTerminal;
				peripheral.addUpdateListener(this);
			}
		}
		
		public int getColor(){
			if ( !isLinked )
				return 0x4D4D4D;
			
			if ( isHilighted && TerminalRenderer.getBlink() )
				return 0xFF2020;
			
			return 0xFFFFFF;
		}
		
		public ItemStack getIcon(){
			return icon;
		}
		
		public String getTooltip(){
			return label;
		}

		//This shall be only called by terminal entities
		public void onUpdate() {
			container.updateTerminal(index);
		}
	}
	
	protected final boolean isServer;
	protected final EntityPlayer owner;
	
	//TODO: I shall probably make these two protected!
	public TerminalTab[] tabs = new TerminalTab[6];
	public int activeTabID = -1;
	
	protected ItemStack terminal;
	
	protected InventoryPlayer playerInv;
	protected InventoryBasic crystalInv;
	
	//Serverside constructor
	public ContainerTerminal( EntityPlayer player, ItemStack item ){
		this( player, true );
		
		this.terminal = item;
		loadItemData(item);
	}
	
	//Clientside constructor
	public ContainerTerminal( EntityPlayer player, boolean isServer ){
		this.isServer	= isServer;
		this.owner		= player;
		
		this.playerInv	= player.inventory;
		this.crystalInv	= new InventoryBasic("Crystals", false, 6);
		
		for ( int index = 0; index < tabs.length; index++ )
			tabs[index] = new TerminalTab(index, this);

		int anchorX = 42;
		int anchorY = 104;
		
		//Player main inventory
		for ( int lineID = 1; lineID < 4; lineID++ )
			for ( int slot = 0; slot < 9; slot++ )
				addSlotToContainer( new Slot(playerInv, lineID *9 + slot, anchorX + slot *18, anchorY + lineID *18) );

		//Player hotbar
		anchorY += 76;
		
		for ( int slot = 0; slot < 9; slot++ )
			addSlotToContainer( new Slot(playerInv, slot, anchorX + slot *18, anchorY) );
		
		//Crystals
		anchorX += 180;
		anchorY -= 58;

		for ( int lineID = 0; lineID < 3; lineID++ )
			for ( int slot = 0; slot < 2; slot++ )
				addSlotToContainer( new SlotCrystal(crystalInv, lineID *2 + slot, anchorX + slot *18, anchorY + lineID *18) );
	}
	
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
	
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack transfer = null;
		Slot slot = (Slot) this.inventorySlots.get(index);
		
		if ( slot != null && slot.getHasStack() ){
			ItemStack item = slot.getStack();
			
			transfer = item.copy();
		
			if ( index >= 36 ){ //Move crystals to the player inv
				
				if ( !mergeItemStack(item, 0, 36, true) )
					return null;
				
			} else {
				
				if ( !SlotCrystal.isValid(item) || !mergeItemStack(item, 36, inventorySlots.size(), false) ) //Move from the player inv
					return null;
			
			}
		
			if ( item.stackSize == 0 ) { //The item is consumed completely
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}
		}
		
        return transfer;
	}

	/*
	 * Events 
	 */
	
	//Hacky event, so the client would receive terminals AFTER they opened the GUI
	public void addCraftingToCrafters(ICrafting crafting) {
		super.addCraftingToCrafters(crafting);
		
		sendInitialData();
	}
	
	public void onCraftGuiClosed(EntityPlayer player) {
		super.onCraftGuiClosed(player);
		
		if ( terminal != null ){
			saveItemData(terminal);
		
			//Notify closed terminals
			for ( int index = 0; index < tabs.length; index++ ){
				TerminalTab tab = tabs[index];
				
				if ( tab.isLinked && tab.peripheral != null )
					tab.peripheral.queueEvent("pda_close");
			}
		}
	}

	/*
	 * Stack manipulation
	 */
	protected static final String CRYSTAL_TAG 	= "crystalFreq";
	protected static final String COUNT_TAG 	= "crystalCount";
	protected static final String FOCUS_TAG		= "focusedTab";
	
	protected void loadItemData( ItemStack item ){
		if ( item != null && item.hasTagCompound() ){
			NBTTagCompound data = item.getTagCompound();
			
			//Load crystals, and setup connections
			int[] frequencies = data.getIntArray( CRYSTAL_TAG );
			int count = Math.min( crystalInv.getSizeInventory(), frequencies.length );
			
			Vec3 plyPos = Vec3.createVectorHelper( owner.posX, owner.posY, owner.posZ );
			
			for ( int index = 0; index < count; index++ ){
				int freq = frequencies[index];
				
				if ( freq > 0 ){
					//Set crystal in place
					crystalInv.setInventorySlotContents(index, ItemQuartz.newCrystal(freq) );
			
					//Add reachable terminals
					PeripheralTerminal term = SLPMod.proxy.network.querySingleDevice(plyPos, freq);
					if ( term != null )	
						setTerminal(index, term);

				}
			}
			
			//Load misc data
			setActiveTab(data.getInteger(FOCUS_TAG));
			
			detectAndSendChanges();
		}
	}
	
	protected void saveItemData( ItemStack item ){
		NBTTagCompound data = new NBTTagCompound();
		
		//Save crystals
		int limit = crystalInv.getSizeInventory();
		
		int[] freqencies = new int[limit];
		int count = 0;
		
		for ( int index = 0; index < limit; index++ ){
			ItemStack crystal = crystalInv.getStackInSlot(index);
			
			if ( crystal == null ) {
				freqencies[index] = -1;
			} else {
				freqencies[index] = crystal.getItemDamage();
				count++;
			}
		}
		
		if ( count > 0 ){
			data.setInteger( COUNT_TAG, count );
			data.setIntArray( CRYSTAL_TAG, freqencies );
		}
		
		//Save misc data
		data.setInteger(FOCUS_TAG, activeTabID);
		
		item.setTagCompound(data);
	}
	
	
	/*
	 * Network - Terminal management
	 */
	protected static final int ID_TERM_CREATE	= 0x00;
	protected static final int ID_TERM_UPDATE	= 0x01;
	
	protected static final int ID_EVENT_KEY		= 0x02;
	protected static final int ID_EVENT_MOUSE	= 0x03;
	
	protected static final int ID_ACTIVE_TAB	= 0x10;
	
	public boolean isValidTarget( int index ){
		return 0 <= index && index < tabs.length;
	}
	
	//Server
	public void setTerminal( int id, PeripheralTerminal terminal ){
		if ( !isValidTarget(id) )
			return;
		
		tabs[id].setTerminal(terminal);
		terminal.queueEvent("pda_open");
	}
	
	protected void sendInitialData(){
		for ( int index = 0; index < tabs.length; index++ ){
			TerminalTab tab = tabs[index];
			
			if ( tab.isLinked ){
				Payload payload = CommonProxy.newContainerUpdate(this);
					payload.writeByte( ID_TERM_CREATE );
					
					payload.writeByte( index );
					payload.writeInt( tab.peripheral.getFreq() );
					
					tab.peripheral.writeNewPayload(payload);
				
				payload.sendTo(owner);
			}
		}
		
		setActiveTab(activeTabID);
	}
	
	public void updateTerminal( int id ){
		if ( !isValidTarget(id) )
			return;
		
		Payload payload = CommonProxy.newContainerUpdate(this);
			payload.writeByte( ID_TERM_UPDATE );
			payload.writeByte( id );
			
			tabs[id].peripheral.writeUpdatePayload(payload);
			
		payload.sendTo(owner);
	}
	
	//Client
	public void sendKeyEvent( int id, char chr, int key ){
		if ( !isValidTarget(id) )
			return;
		
		Payload payload = CommonProxy.newContainerUpdate(this);
			payload.writeByte( ID_EVENT_KEY );
			payload.writeByte( id );
			
			payload.writeChar(chr);
			payload.writeInt(key);
			
		payload.sendToServer();
	}
	
	public void sendMouseEvent( int id, int event, int charX, int charY ){
		if ( !isValidTarget(id) )
			return;
		
		Payload payload = CommonProxy.newContainerUpdate(this);
			payload.writeByte( ID_EVENT_MOUSE );
			payload.writeByte( id );
			
			payload.writeByte(event);
			payload.writeShort(charX);
			payload.writeShort(charY);
			
		payload.sendToServer();
	}
	
	//Shared
	public void setActiveTab( int id ){
		if ( !isValidTarget(id) || !tabs[id].isLinked  )
			return;
		
		activeTabID = id;
		
		Payload payload = CommonProxy.newContainerUpdate(this);
			payload.writeByte( ID_ACTIVE_TAB );
			payload.writeByte( id );
		
		if ( isServer ) {
			payload.sendTo(owner);
		} else {
			payload.sendToServer();
		}
	}
	
	//Shared
	public void handlePacket(DataInputStream stream, EntityPlayer player) throws IOException {
		int type 	= stream.readByte();
		int index	= stream.readByte();
		
		if ( !isValidTarget(index) ){ //Invalid target
			CommonProxy.protocolViolation(player);
			return;
		}
		
		//Handle shared events
		if ( type == ID_ACTIVE_TAB ){
			activeTabID = index;
			return;
		}
		
		//Terminal based events
		TerminalTab tab = tabs[ index ];
		
		if ( isServer ){
			PeripheralTerminal terminal = tab.peripheral;
			
			if ( terminal == null || !tab.isLinked )
				return; //Really should not happen
			
			switch( type ){
				case ID_EVENT_KEY: //Key press events
					char chr = stream.readChar();
					int  key = stream.readInt();
					
					if ( key >= 0 )
						terminal.queueEvent( "pda_key", key );
					
					if ( ChatAllowedCharacters.isAllowedCharacter(chr) )
						terminal.queueEvent( "pda_char", "" + chr );

					break;
			
				case ID_EVENT_MOUSE: //Mouse events
					byte event = stream.readByte();
					
					int charX = stream.readShort() +1;
					int charY = stream.readShort() +1;
					
					switch( event ){
						
						case 0: //Mouse click
						case 1:
						case 2:
							terminal.queueEvent("pda_click", event +1, charX, charY );
							break;
						
						//Mouse scroll
						case 3:	terminal.queueEvent("pda_scroll",  1 );	break;
						case 4:	terminal.queueEvent("pda_scroll", -1 );	break;
							
						case 5: //Mouse drag
						case 6:
						case 7:
							terminal.queueEvent("pda_drag", event -5 +1, charX, charY );
							break;
						
					}
					break;
					
				
			}
		} else {
			switch( type ){
				case ID_TERM_CREATE: //Terminal creation, and initial sync
					int freq = stream.readInt();
					
					tab.icon  = ItemQuartz.newCrystal(freq);
					tab.label = "Freq: " + freq;
							
					tab.terminal = Terminal.readNewPayload(stream);
					
					tab.isLinked = true;
					break;
			
				case ID_TERM_UPDATE: //Line updates
					tab.terminal.readUpdatePayload(stream);
					tab.isHilighted = true;
					break;
			}
		}
		
	}

}
