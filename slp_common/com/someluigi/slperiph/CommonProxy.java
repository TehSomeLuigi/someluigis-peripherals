package com.someluigi.slperiph;


import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.someluigi.slperiph.ccportable.FileUtils;
import com.someluigi.slperiph.ccportable.shared.IPacketHandler;
import com.someluigi.slperiph.ccportable.shared.PayloadManager.Payload;
import com.someluigi.slperiph.ccportable.shared.WirelessNetwork;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommonProxy {
	public static final String TEX_ITEMS  = "/hu/mentlerd/ccportable/pp_items.png";
	public static final String TEX_BLOCKS = "/hu/mentlerd/ccportable/pp_blocks.png";
	
	public WirelessNetwork network = new WirelessNetwork();
	
	public static void protocolViolation( EntityPlayer player ){
		System.err.println( player + " - Protocol violation" );
	}
	
	public static <T extends TileEntity> T getTileEntity( World world, int x, int y, int z, Class<T> clazz ){
		if ( world.blockExists(x, y, z) ){
			TileEntity tile = world.getBlockTileEntity(x, y, z);
			
			if ( clazz.isAssignableFrom( tile.getClass() ) )
				return clazz.cast( tile );
		}
		
		return null;
	}
	
	public static Payload newTileUpdate( TileEntity tile ){		
		Payload payload = new Payload( Payload.TILE_UPDATE );
		
		payload.writeInt( tile.xCoord );
		payload.writeShort( tile.yCoord );
		payload.writeInt( tile.zCoord );
				
		return payload;
	}
	
	public static Payload newContainerUpdate( Container container ){
		Payload payload = new Payload( Payload.GUI_UPDATE );
		
		payload.writeInt( container.windowId );
		
		return payload;
	}
	
	public void init(){
	}
	
	/*
	 * Network
	 */
	public void handlePacket( int type, DataInputStream stream, EntityPlayer player ) throws IOException{		
		switch( type ){
			
			//Tile entity updates
			case Payload.TILE_UPDATE:
				World world = getPlayerWorld(player);
				
				int x = stream.readInt();
				int y = stream.readShort();
				int z = stream.readInt();
				
				if ( world.blockExists(x, y, z) ){
					TileEntity tile = world.getBlockTileEntity(x, y, z);
					
					dispatchPacket(tile, stream, player);
				}
				break;
				
			//Container updates
			case Payload.GUI_UPDATE:
				int windowID = stream.readInt();
				
				Container window = player.openContainer;
				if ( window != null && window.windowId == windowID )
					dispatchPacket(window, stream, player);
				
				break;
				
			//Protocol violation
			default:
				protocolViolation(player);
				break;
		}
	}
	
	protected void dispatchPacket( Object obj, DataInputStream stream, EntityPlayer player ) throws IOException{
		if ( obj != null && obj instanceof IPacketHandler )
			((IPacketHandler) obj).handlePacket(stream, player);
	}
	
	public World getPlayerWorld( EntityPlayer player ){
		return player.worldObj;
	}
	
	/*
	 * Filesystem
	 */
	public File getBaseFolder(){
		return FMLCommonHandler.instance().getMinecraftServerInstance().getFile(".");
	}
	
	public File getModJar(){
		return FMLCommonHandler.instance().findContainerFor( SLPMod.instance ).getSource();
	}
	
	public void unpackResourceFolder( String from, String into ){
		File mod	= getModJar();	
		File target	= new File( getBaseFolder(), into );
		
		try {
			
			if ( mod.isDirectory() ){
				mod = new File( mod, from );
				
				FileUtils.copyFolder(mod, target, true);
			} else {
				FileUtils.unzip(mod, from, target);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println( "Unable to extract resource folder: " + from );
		}
	}

}
