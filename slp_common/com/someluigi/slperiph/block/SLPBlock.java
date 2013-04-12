package com.someluigi.slperiph.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import com.someluigi.slperiph.ccportable.shared.TileEntityTransmitter;
import com.someluigi.slperiph.tileentity.TileEntityHTTPD;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computer.api.ComputerCraftAPI;

public class SLPBlock extends Block {

    public Icon heatVentIcon;

    public SLPBlock(int par1, int par2, Material par3Material) {
        super(par1, Material.rock);
        this.setCreativeTab(ComputerCraftAPI.getCreativeTab());
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    @Override
    public void onBlockAdded(World par1World, int par2, int par3, int par4) {
        par1World.setBlockTileEntity(
                par2,
                par3,
                par4,
                this.createNewTileEntity(par1World,
                        par1World.getBlockMetadata(par2, par3, par4)));
    }

    // this.o

    /**
     * Returns the TileEntity used by this block.
     */
    public TileEntity createNewTileEntity(World world, int metadata) {
        switch (metadata) {
            case 0:
                return new TileEntityHTTPD();
            case 1: // PP: Transmitter
                return new TileEntityTransmitter();
        }
        return null;
    }

    /**
     * each class overrides this to return a new <className>
     */
    public TileEntity createNewTileEntity(World var1) {
        return new TileEntityHTTPD();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    // Client side only
    public Icon getBlockTextureFromSideAndMetadata(int side, int meta) {
        // Tells
        // it
        // which
        // texture
        // from
        // the
        // sprite
        // sheet
        switch (meta) {
            case 0: // HTTP Server
                switch (side) {
                    case 1:
                        return this.heatVentIcon;
                    case 0:
                        return this.heatVentIcon;
                }
                return this.blockIcon;
            case 1: // PP: Transmitter
                // return;

        }
        return this.blockIcon;
    }

    public String getTextureFile() {
        return "com/someluigi/slperiph/res/textures.png";
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getSubBlocks(int i, CreativeTabs tab, List list) {
        list.add(new ItemStack(i, 1, 0));
    }

    @Override
    public int damageDropped(int i) {
        return i;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister ir) {
        this.blockIcon = ir
                .registerIcon("slperiph:blockhttpserver-server");
        this.heatVentIcon = ir
                .registerIcon("slperiph:blockhttpserver-vents");
    }

}
