/*
package com.someluigi.slperiph.ccportable;

import com.someluigi.slperiph.CommonProxy;
import com.someluigi.slperiph.ccportable.shared.BlockAntenn;
import com.someluigi.slperiph.ccportable.shared.BlockTransmitter;
import com.someluigi.slperiph.ccportable.shared.GuiManager;
import com.someluigi.slperiph.ccportable.shared.ItemPDA;
import com.someluigi.slperiph.ccportable.shared.ItemQuartz;
import com.someluigi.slperiph.ccportable.shared.PayloadManager;
import com.someluigi.slperiph.ccportable.shared.RecipeQuartzMix;
import com.someluigi.slperiph.ccportable.shared.TileEntityTransmitter;
import com.someluigi.slperiph.ccportable.shared.UpgradeTransmitter;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import dan200.computer.api.ComputerCraftAPI;
import dan200.turtle.api.TurtleAPI;

/*
@Mod(
	modid = "PortablePeripherals",
	name = "PortablePeripherals",
	dependencies = "required-after:ComputerCraft;after:CCTurtle",
	
	version = "1.0b"
)

@NetworkMod(
	serverSideRequired = true, 
	clientSideRequired = true, 
	
	channels = { PayloadManager.CHANNEL_ID }, 
	packetHandler = PayloadManager.class
)
*

public class PortablePeripherals {
	
	@Instance
	public static PortablePeripherals instance;
	
	/*
	@SidedProxy(
		clientSide = "hu.mentlerd.ccportable.client.ClientProxy", 
		serverSide = "hu.mentlerd.ccportable.shared.CommonProxy"
	)
	public static CommonProxy proxy;
	*

	public static CreativeTabs creativeTab;
	
	//Config
	public static class Config{
		public static int blockTransmitterID = 0;	
		public static int blockAntennID = 0;	
		
		public static int itemTerminalID 	= 0;
		public static int itemQuartzID 		= 0;
		
		public static int turtleRange		= 256;
		
		public static int minTransmitterRange = 128;
		public static int maxTransmitterRange = 640;
	}
	
	public static BlockTransmitter	blockTransmitter;
	public static BlockAntenn blockAntenn;

	public static Item itemPDA;
	public static Item itemQuartz;
	
	@PreInit
	public void preInit( FMLPreInitializationEvent event ) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		
		//Blocks
		Config.blockTransmitterID	= config.getBlock( "transmitterBlockID", 980).getInt();
		Config.blockAntennID		= config.getBlock( "antennBlockID", 981).getInt();
	
		//Items
		Config.itemTerminalID	= config.getItem("terminalItemID", 4200).getInt();
		Config.itemQuartzID		= config.getItem("quartzItemID", 4201).getInt();
		
		//Range
		Config.turtleRange 		= config.get("general", "turtleRange", Config.turtleRange).getInt();
		
		Config.minTransmitterRange = config.get("general", "minRange", Config.minTransmitterRange).getInt();
		Config.maxTransmitterRange = config.get("general", "maxRange", Config.maxTransmitterRange).getInt();
	
		config.save();
	}

	@Init
	public void load( FMLInitializationEvent event ) {
		System.out.println( "Loading PortablePeripherals v" + version() );
		
		creativeTab			= ComputerCraftAPI.getCreativeTab();
		blockTransmitter	= new BlockTransmitter(Config.blockTransmitterID);
		blockAntenn			= new BlockAntenn(Config.blockAntennID);
		
		itemPDA		= new ItemPDA(Config.itemTerminalID);
		itemQuartz	= new ItemQuartz(Config.itemQuartzID);
		
		//Items
		GameRegistry.registerBlock(blockTransmitter, "ccportable.transmitter" );
		GameRegistry.registerBlock(blockAntenn, 	 "ccportable.antenn" );
		
		GameRegistry.registerItem(itemPDA, 		"ccportable.pda");
		GameRegistry.registerItem(itemQuartz, 	"ccportable.quartz");
		
		//Translation
		LanguageRegistry.addName(blockTransmitter, 	"Wireless Transmitter");
		LanguageRegistry.addName(blockAntenn, 		"Antenna");
		
		LanguageRegistry.addName(itemPDA, 		"Wireless Terminal");
		LanguageRegistry.addName(itemQuartz, 	"Quartz Crystal");
		
		//Misc
		GameRegistry.registerTileEntity(TileEntityTransmitter.class, "ccportable.tile.transmitter");
				
		//Recipes
		GameRegistry.addRecipe(new ItemStack( blockTransmitter ), 
				new String[]{ "P", "B", "D" }, 
				'P', Item.enderPearl,
				'B', Block.blockSteel,
				'D', Item.diamond
		);

		GameRegistry.addRecipe(new ItemStack( blockAntenn ), 
				new String[]{ "OIO", "ODO", "OIO" }, 
				'I', Item.ingotIron,
				'D', Item.diamond,
				'O', Block.obsidian
		);

		GameRegistry.addRecipe(new ItemStack( itemPDA ), 
				new String[]{ "P", "I", "D" }, 
				'P', Item.enderPearl,
				'I', Item.ingotIron,
				'D', Item.diamond
		);
		
		
		//Quartz
		GameRegistry.addRecipe( new ItemStack( itemQuartz, 1 ), new String[]{ "X", "X", "X" }, 'X', Block.glass );
		GameRegistry.addRecipe( new RecipeQuartzMix() );
		
		//GUI
		NetworkRegistry.instance().registerGuiHandler(this, new GuiManager());
		
		//Turtles
		TurtleAPI.registerUpgrade( new UpgradeTransmitter() );
		
		proxy.unpackResourceFolder( "com/someluigi/slperiph/lua", "mods/ComputerCraft/lua/rom/programs" );
		proxy.init();
	}

	protected String version(){
		return getClass().getAnnotation( Mod.class ).version();
	}
	
}
*/