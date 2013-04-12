package com.someluigi.slperiph.client;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.someluigi.slperiph.CommonProxy;

import cpw.mods.fml.common.network.Player;

public class ClientProxy extends CommonProxy{
	
	public void init() {
		//MinecraftForgeClient.preloadTexture( TEX_BLOCKS );
		//MinecraftForgeClient.preloadTexture( TEX_ITEMS );
	}
	
	public World getPlayerWorld(Player player) {
		return ((EntityPlayer) player).worldObj;
	}

	public File getBaseFolder(){
		return Minecraft.getMinecraftDir();
	}
	
}
