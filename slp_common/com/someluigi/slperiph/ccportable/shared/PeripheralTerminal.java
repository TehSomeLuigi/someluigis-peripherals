package com.someluigi.slperiph.ccportable.shared;


import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import com.someluigi.slperiph.SLPMod;
import com.someluigi.slperiph.SLPMod.Config;
import com.someluigi.slperiph.ccportable.shared.PayloadManager.Payload;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IHostedPeripheral;
import dan200.turtle.api.ITurtleAccess;

public class PeripheralTerminal implements IHostedPeripheral{
	
	protected static Object[] wrap( Object ... values ){
		return values;
	}
	
	protected IComputerAccess computer;
	protected ITurtleAccess turtle;
	
	protected Terminal terminal;
	protected Vec3 staticPos;
	
	protected boolean isDirty = false;
	
	protected int range;
	protected int freq;	
	
	public PeripheralTerminal(){
		range = Config.minTransmitterRange;	 
		terminal = new Terminal( 51, 19, true );
	}
	
	public PeripheralTerminal( ITurtleAccess host ){
		range = Config.turtleRange;
		turtle = host;
		
		terminal = new Terminal( 39, 13, false );
	}
	
	/*
	 * Event based development
	 */
	protected Set<IUpdateListener> listeners = Collections.newSetFromMap( new WeakHashMap<IUpdateListener, Boolean>() );
	
	public void addUpdateListener( IUpdateListener listener ){
		listeners.add(listener);
	}
	public void removeUpdateListener( IUpdateListener listener ){
		listeners.remove(listener);
	}

	public void update() {
		if ( isDirty ){
			isDirty = false;

			for ( IUpdateListener listener : listeners )
				listener.onUpdate();
		}
	}
	
	/*
	 * Wireless network
	 */
	public Vec3 getTransmitterPos(){
		if ( turtle != null )
			return turtle.getPosition();
		
		return staticPos;
	}
	
	public int getFreq(){
		return freq;
	}
	
	public int getRange(){
		return Config.maxTransmitterRange;
	}
	
	public void setFreq( int freq ){
		WirelessNetwork network = SLPMod.proxy.network;
		
		network.removeDevice(this);
		this.freq = freq;
		
		if ( freq > 0 )
			network.addDevice(this);
	}

	
	public boolean canConnectTo( Vec3 pos ){
		Vec3 transPos = getTransmitterPos();
		
		if ( transPos == null || freq == 0 )
			return false;
		
		return transPos.distanceTo(pos) < getRange();
	}
	
	/*
	 * Network
	 */
	public void writeNewPayload(Payload payload) {
		terminal.writeNewPayload(payload);
	}
	public void writeUpdatePayload(Payload payload) {
		terminal.writeUpdatePayload(payload);
	}
	
	/*
	 * Helpers
	 */
	public void queueEvent( String id, Object ... args ){
		if ( computer != null )
			computer.queueEvent(id, args);
	}
	
	public String getType() {
		return "transmitter";
	}

	public boolean canAttachToSide(int side) {
		return true;
	}
	
	public void attach(IComputerAccess computer) {
		this.computer = computer;
	}
	public void detach(IComputerAccess computer) {
		this.computer = null;
	}

	public String[] getMethodNames() {
		return new String[]{ 
			//Terminal
			"isColor",	
			"isColour",
			
			"getSize",
				
			"write",
			"clear",
			"clearLine",
			
			"getCursorPos",
			"setCursorPos",
			"setCursorBlink",
			
			"scroll",
			
			"setTextColor",
			"setTextColour",
			
			"setBackgroundColor",
			"setBackgroundColour",
			
			//Peripheral
			"getFreq",
			"getFreqency",
			"getRange",
		
			//Peripheral - Turtle
			"setCrystal"
		};
	}

	public Object[] callMethod(IComputerAccess computer, int method, Object[] args) throws Exception {
		
		if ( terminal == null )
			throw new Exception( "Terminal is not set up. Programming error!" );
		
		switch( method ){
			case 0: //isColor
			case 1: //isColour
				return wrap( terminal.isColorSupported );
		
			case 2: //getSize
				return wrap( terminal.w, terminal.h );
				
			case 3: //write	
				terminal.write( args[0].toString() );
				isDirty = true;
				return null;
				
			case 4: //clear
				terminal.clear();
				isDirty = true;
				break;

			case 5: //clearLine
				terminal.clearLine();
				isDirty = true;
				break;

			case 6: //getCursorPos
				return wrap( terminal.getCursorX() +1, terminal.getCursorY() +1 );
			
			case 7: //setCursorPos
				terminal.setCursorPos(
					((Double) args[0]).intValue() -1, 
					((Double) args[1]).intValue() -1
				);
				break;
				
			case 8: //setCursorBlink
				terminal.cursorBlink = ((Boolean) args[0]);
				isDirty = true;
				break;
				
			case 9: //scroll
				terminal.scroll( ((Double) args[0]).intValue() );
				isDirty = true;
				break;
				
			case 10: //setTextColor
			case 11: //setTextColour
				terminal.colorText = Terminal.parseColor( ((Double) args[0]).intValue() );
				break;
				
			case 12: //setBackgroundColor
			case 13: //setBackgroundColour
				terminal.colorBackground = Terminal.parseColor( ((Double) args[0]).intValue() );
				break;
				
			case 14: //getFreq
			case 15: //getFrequency
				return wrap( freq );
				
			case 16: //getRange
				return wrap( range );
				
			case 17: //setCrystal
				if ( turtle == null )
					throw new Exception( "Not a turtle!" );
				
				ItemStack selected = turtle.getSlotContents( turtle.getSelectedSlot() );
				if ( selected == null || selected.itemID != SLPMod.itemQuartz.itemID )
					return wrap( false );
				
				setFreq( selected.getItemDamage() );
				return wrap( true );
		}
		
		return null;
	}
	
	//Serialization
	protected static final String TAG_FREQ 		= "frequency";
	protected static final String TAG_RANGE		= "range";
	
	public void readFromNBT(NBTTagCompound data) {
		setFreq( data.getInteger(TAG_FREQ) );
		range = Math.max(range, data.getInteger(TAG_RANGE));
	}

	public void writeToNBT(NBTTagCompound data) {
		data.setInteger(TAG_FREQ, freq);
		data.setInteger(TAG_RANGE, range);
	}

	public void setStaticHost( TileEntity tile ) {
		staticPos = Vec3.createVectorHelper(tile.xCoord, tile.yCoord, tile.zCoord);
	}

}
