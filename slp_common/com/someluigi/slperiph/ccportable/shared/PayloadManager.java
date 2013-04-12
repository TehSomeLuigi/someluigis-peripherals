package com.someluigi.slperiph.ccportable.shared;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.someluigi.slperiph.SLPMod;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class PayloadManager implements IPacketHandler{
	public static final String CHANNEL_ID = "CCPP";
	
	public static class Payload extends PayloadStream{
		public static final int TILE_UPDATE = 0x01;
		public static final int GUI_UPDATE	= 0x02;

		public Payload( int type ){
			writeByte(type);
		}
		
		public Packet toPacket(){	
			return new Packet250CustomPayload(CHANNEL_ID, toByteArray());
		}
		
		public void sendToServer(){
			PacketDispatcher.sendPacketToServer( toPacket() );
		}
		
		public void sendTo( EntityPlayer player ){
			PacketDispatcher.sendPacketToPlayer( toPacket(), (Player) player );
		}
	}
	
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		int packetID = -1;
	
		try {
			ByteArrayInputStream array = new ByteArrayInputStream( packet.data );
			DataInputStream stream = new DataInputStream( array );
			
			packetID = stream.readByte();
			
			SLPMod.proxy.handlePacket( packetID, stream, (EntityPlayer) player );
	
			stream.close();
		} catch ( Exception e ) {
			System.err.println( "Error while processing PP Packet #" + packetID + " !" );	
			e.printStackTrace();
		}
	}

}
