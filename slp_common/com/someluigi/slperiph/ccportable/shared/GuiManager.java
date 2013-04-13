package com.someluigi.slperiph.ccportable.shared;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.someluigi.slperiph.CommonProxy;
import com.someluigi.slperiph.SLPMod;
import com.someluigi.slperiph.ccportable.client.GuiTerminal;
import com.someluigi.slperiph.ccportable.client.GuiTransmitter;

import cpw.mods.fml.common.network.IGuiHandler;

public class GuiManager implements IGuiHandler {
	public static final int GUI_TERMINAL	= 0x00;
	public static final int GUI_TRANSMITTER	= 0x01;
	
	public static void openGui( EntityPlayer player, int index, World world, int x, int y, int z ){
		player.openGui(SLPMod.instance, index, world, x, y, z);
	}
	
	public static void openGui( EntityPlayer player, int index ){
		openGui(player, index, player.worldObj, 0, 0, 0);
	}
	

	public Object getServerGuiElement(int index, EntityPlayer player, World world, int x, int y, int z) {
		switch( index ){
			
			//Wireless Terminal
			case GUI_TERMINAL:
				return new ContainerTerminal(player, player.getHeldItem());
			
			//Wireless Transmitter
			case GUI_TRANSMITTER:
				TileEntityTransmitter trans = CommonProxy.getTileEntity(world, x, y, z, TileEntityTransmitter.class);
				
				if ( trans != null )
					return new ContainerTransmitter(player, trans);
				break;
			
		}
		
		return null;
	}

	public Object getClientGuiElement(int index, EntityPlayer player, World world, int x, int y, int z) {
		switch( index ){
			
			//Wireless Terminal
			case GUI_TERMINAL:
				return new GuiTerminal( new ContainerTerminal(player, false) );
			
			//Wireless Transmitter
			case GUI_TRANSMITTER:
				return new GuiTransmitter( new ContainerTransmitter(player, null) );
		
		}
		
		return null;
	}

}
