package com.someluigi.slperiph;

import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.Configuration;

import com.someluigi.slperiph.block.ItemBlockSLP;
import com.someluigi.slperiph.block.SLPBlock;
import com.someluigi.slperiph.server.SLPHTTPServer;
import com.someluigi.slperiph.tileentity.TileEntityHTTPD;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.Mod.ServerStarting;
import cpw.mods.fml.common.Mod.ServerStopping;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "SLPMod", name = "SLs Peripherals", version = "1.0b", dependencies = "after:ComputerCraft;after:CCTurtle")
public class SLPMod {

    public Logger l;

    public static SLPBlock blockSLP;

    public static int bPeriph;

    public static boolean craftHttpd;
    public static boolean debugM;

    public static int httpdPort;
    public static boolean httpdEnabled;

    @PreInit
    public void preinit(FMLPreInitializationEvent evt) {

        this.l = Logger.getLogger("SLPMod");

        Configuration cfg = new Configuration(
                evt.getSuggestedConfigurationFile());

        cfg.load();

        // get Block ID
        bPeriph = cfg.getBlock("block", "peripheral", 2047).getInt(2047);

        // enable crafting recipe?
        craftHttpd = cfg.get("crafting", "httpserver", true,
                "Enable HTTP Server").getBoolean(true);

        // http server port
        httpdPort = cfg
                .get("httpserver",
                        "port",
                        80,
                        "The port that the HTTP Server will run on. Default on the internet is 80, but you may have to change this.")
                .getInt(80);
        httpdEnabled = cfg
                .get("httpserver",
                        "enabled",
                        true,
                        "Whether the HTTP Server should actually run. This does not affect the peripherals in game, apart from making the HTTP Server unusable. This should be disabled if you get a crash, so your blocks will not be cleared and restore when it is fixed.")
                .getBoolean(true);

        debugM = cfg.get("debug", "modeEnabled", false, "Debug mode")
                .getBoolean(false);

        cfg.save();
    }

    // @SidedProxy(clientSide = "com.someluigi.slperiph.client.SLPClientProxy",
    // serverSide = "com.someluigi.slperiph.server.SLPProxy")
    // public static SLPProxy proxy;

    @Init
    public void init(FMLInitializationEvent evt) {

        /*
         * 1.5.1 Testing *
         * 
         * List tl = TextureManager.instance().createTexture(
         * "com/someluigi/slperiph/res/textures.png"); for (Object o : tl) {
         * System.out.print("Texture: " + o.toString()); }
         * 
         * /* End 1.5.1 Testing
         */

        // List tl =
        // TextureManager.instance().createTexture("com/someluigi/slperiph/res/blockhttpserver.png");

        this.l = Logger.getLogger("SLPMod");

        // proxy.registerRenderInfo(); // do stuff like preloading etc

        blockSLP = (SLPBlock) new SLPBlock(bPeriph, 0, Material.rock)
                .setUnlocalizedName("slpPeriph").setLightValue(1F)
                .setResistance(30).setHardness(1F);
        GameRegistry.registerBlock(blockSLP, ItemBlockSLP.class, "slpPeriph"); // register
                                                                               // block
                                                                               // for
                                                                               // ItemBlock

        if (craftHttpd) {
            this.l.info("Adding recipe for HTTP Server Module");

            // HTTP Server Module crafting
            GameRegistry.addRecipe(new ItemStack(blockSLP),
                    new Object[] { "rtr", "gbg", "gig", Character.valueOf('r'),
                            Item.redstone, Character.valueOf('t'),
                            Block.torchRedstoneIdle, Character.valueOf('i'),
                            Item.ingotIron, Character.valueOf('g'),
                            Item.ingotGold, Character.valueOf('b'),
                            Block.blockSteel // this is actually IRON block
                    });
            GameRegistry.addRecipe(new ItemStack(blockSLP),
                    new Object[] { "rtr", "gbg", "gig", Character.valueOf('r'),
                            Item.redstone, Character.valueOf('t'),
                            Block.torchRedstoneActive, Character.valueOf('i'),
                            Item.ingotIron, Character.valueOf('g'),
                            Item.ingotGold, Character.valueOf('b'),
                            Block.blockSteel // this is actually IRON block
                    });

            TileEntity.addMapping(TileEntityHTTPD.class, "slp-phttpd");

            LanguageRegistry.addName(new ItemStack(blockSLP, 1, 0),
                    "HTTP Server Service Module");

        } else {
            this.l.info("Crafting HTTP Server Module disabled");
        }

    }

    @ServerStarting
    public void serverstarting(FMLServerStartingEvent evt) {

        this.l = Logger.getLogger("SLPMod");

        if (httpdEnabled) {

            this.l.info("Starting Simple HTTP Server NOW! - on port "
                    + httpdPort);
            SLPHTTPServer.start(httpdPort);
            this.l.info("HTTP Server is started.");
            this.l.info("HTTP Server is using simpleframework, simpleframework.org");

        } else {

            this.l.info("HTTP Server is disabled in the config, will not start.");

        }

    }

    @ServerStopping
    public void serverstopping(FMLServerStoppingEvent evt) {
        SLPHTTPServer.stop();
    }

}
