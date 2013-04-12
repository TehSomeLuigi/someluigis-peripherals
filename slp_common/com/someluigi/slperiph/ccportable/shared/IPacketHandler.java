package com.someluigi.slperiph.ccportable.shared;

import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

public interface IPacketHandler {	
	public void handlePacket( DataInputStream stream, EntityPlayer player ) throws IOException;
}
