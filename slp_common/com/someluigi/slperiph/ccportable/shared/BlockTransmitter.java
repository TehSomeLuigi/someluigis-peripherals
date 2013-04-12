package com.someluigi.slperiph.ccportable.shared;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.someluigi.slperiph.SLPMod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computer.api.ComputerCraftAPI;

public class BlockTransmitter extends Block {
    
    public Icon equiv_0, equiv_1, equiv_2;
    public Icon turtle_equiv_3;
    
    
	public static final int MAX_ANTENN_LENGTH = 5;
	
	public static void setRangeAmplifier( World world, int x, int y, int z, int len ){
		int range = SLPMod.Config.minTransmitterRange;
		len -= 1;
		
		if ( len > 0 ){
			double max = SLPMod.Config.maxTransmitterRange - SLPMod.Config.minTransmitterRange;
			double amp = MAX_ANTENN_LENGTH - len;
			
			range += MathHelper.floor_double( max / amp );
		}
		
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		if ( tile instanceof TileEntityTransmitter )
			((TileEntityTransmitter) tile).terminal.range = range;
	}
	
	public BlockTransmitter( int typeID ) {
		super(typeID, Material.circuits);
		
		setCreativeTab(ComputerCraftAPI.getCreativeTab());
		
		setUnlocalizedName("slp.pp.BlockTransmitter");
		
		setHardness( 0.5f );
		disableStats();
	}
	
	public boolean hasTileEntity(int data) {
		return true;
	}
	
	public TileEntity createTileEntity( World world, int data ) {
		return new TileEntityTransmitter();
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving living, ItemStack is) {
		
		if ( !world.isRemote ){
			int dir = MathHelper.floor_double( living.rotationYaw * 4 / 360 + 0.5 ) & 3;
			
			switch( dir ){
				case 0:		dir = 2;	break;
				case 1:		dir = 5;	break;
				case 2:		dir = 3;	break;
				case 3:		dir = 4;	break;
			}
						
			world.setBlockMetadataWithNotify(x, y, z, dir, 0);
		}
		
		super.onBlockPlacedBy(world, x, y, z, living, is);
	}
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float sX, float sY, float sZ) {
		GuiManager.openGui(player, GuiManager.GUI_TRANSMITTER, world, x, y, z);
		return true;
	}
	
	public Icon getBlockTextureFromSideAndMetadata(int side, int meta) {
		if ( side == (meta & 7) )
			return equiv_2;
		
		if ( side < 2 )
			return equiv_0;
		
		return equiv_1;
	}
	
	
	@Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister ir) {
        this.equiv_0 = ir
                .registerIcon("slperiph:pp-0");
        this.equiv_1 = ir
                .registerIcon("slperiph:pp-1");
        this.equiv_2 = ir
                .registerIcon("slperiph:pp-2");
        this.turtle_equiv_3 = ir
                .registerIcon("slperiph:pp-3");
    }
	
	
}
