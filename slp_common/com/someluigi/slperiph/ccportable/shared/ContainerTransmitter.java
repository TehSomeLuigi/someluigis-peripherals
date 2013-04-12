package com.someluigi.slperiph.ccportable.shared;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerTransmitter extends Container {
	
	protected TileEntityTransmitter transmitter;
	protected InventoryBasic crystalInv = new InventoryBasic("Crystals", false, 1);
	
	public ContainerTransmitter( EntityPlayer player, TileEntityTransmitter transmitter ){
		this.transmitter = transmitter;
		
		InventoryPlayer playerInv = player.inventory;
		
		int anchorX = 8;
		int anchorY = 51;
		
		//Player main inventory
		for ( int lineID = 1; lineID < 4; lineID++ )
			for ( int slot = 0; slot < 9; slot++ )
				addSlotToContainer( new Slot(playerInv, lineID *9 + slot, anchorX + slot *18, anchorY + lineID *18) );

		//Player hotbar
		anchorY += 76;
		
		for ( int slot = 0; slot < 9; slot++ )
			addSlotToContainer( new Slot(playerInv, slot, anchorX + slot *18, anchorY) );
		
		//Crystal
		anchorX = 8;
		anchorY = 19;
		
		addSlotToContainer( new SlotCrystal(crystalInv, 0, anchorX, anchorY) );
	
		//Add the crystal
		if ( transmitter != null ){
			int freq = transmitter.terminal.getFreq();
			
			if ( freq != 0 )
				crystalInv.setInventorySlotContents(0, ItemQuartz.newCrystal(freq) );
		}
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
	
	public void onCraftGuiClosed(EntityPlayer player) {	
		super.onCraftGuiClosed(player);
		
		if ( transmitter != null ){
			ItemStack crystal = crystalInv.getStackInSlotOnClosing(0);
			
			transmitter.terminal.setFreq( crystal == null ? 0 : crystal.getItemDamage() );
		}
	}
	
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

}
