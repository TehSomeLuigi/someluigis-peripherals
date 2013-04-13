package com.someluigi.slperiph.ccportable.shared;

import com.someluigi.slperiph.ComputerManipulation;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityTransmitter extends TileEntity implements IPeripheral{
	public PeripheralTerminal terminal = new PeripheralTerminal();
	
	//Serializaton
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		
		terminal.readFromNBT(data);
	}
	
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		
		terminal.writeToNBT(data);
	}
	
	public void updateEntity() {
		terminal.update();
	}
	
	/*
	 * CC Peripheral
	 */
	public String getType() {
		return terminal.getType();
	}

	public boolean canAttachToSide(int side) {
		return terminal.canAttachToSide(side);
	}
	
	public void attach(IComputerAccess computer) {
		terminal.setStaticHost(this);	
		terminal.attach(computer);
		ComputerManipulation.mountDemoDir(computer);
	}
	public void detach(IComputerAccess computer) {
		terminal.detach(computer);
	}
	
	public String[] getMethodNames() {
		return terminal.getMethodNames();
	}

	public Object[] callMethod(IComputerAccess computer, int method, Object[] args) throws Exception {
		return terminal.callMethod(computer, method, args);
	}

}
