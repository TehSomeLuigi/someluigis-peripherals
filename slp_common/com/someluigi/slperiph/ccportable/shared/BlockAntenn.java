package com.someluigi.slperiph.ccportable.shared;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

import com.someluigi.slperiph.SLPMod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computer.api.ComputerCraftAPI;

public class BlockAntenn extends Block {
	
    
    public Icon equiv_0;
    public Icon equiv_4;
    
    
    
    
	public BlockAntenn( int typeID ) {
		super(typeID, Material.circuits);
		
		setCreativeTab(ComputerCraftAPI.getCreativeTab());
		
		setHardness( 0.4f );
		disableStats();
		
		setUnlocalizedName("slp.pp.BlockAntenn");
		
		setBlockBounds(0.375f, 0, 0.375f, 0.625f, 1, 0.625f);
	}

	public void updateAntenn(World world, int x, int y, int z) {
		if ( world.isRemote )
			return;
		
		int transY = -1;
		int len = 1;
		
		while( len < BlockTransmitter.MAX_ANTENN_LENGTH ){
			int typeID = world.getBlockId(x, y - len, z);
			
			if ( typeID == SLPMod.blockTransmitter.blockID ){
				transY = y - len;
				break;
			}
			
			if ( typeID != blockID )
				return;	
			
			len++;
		}
		
		if ( transY == -1 ) //No transmitter found
			return;
		
		while ( len < BlockTransmitter.MAX_ANTENN_LENGTH ){
			if ( world.getBlockId(x, transY + len, z) != blockID )
				break;
			
			len++;
		}
		
		BlockTransmitter.setRangeAmplifier(world, x, transY, z, len);
	}

	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving living) {
		updateAntenn(world, x, y, z);
	}	
		
	public void breakBlock(World world, int x, int y, int z, int typeID, int meta) {
		updateAntenn(world, x, y, z);
		
		super.breakBlock(world, x, y, z, typeID, meta);
	}
	
	/*
	 * Rendering
	 */
	public boolean isOpaqueCube() {
		return false;
	}
	
	public Icon getBlockTextureFromSideAndMetadata(int side, int meta) {	
		if ( side < 2 )
			return this.equiv_0; // MC 1.5 port
		
		return this.equiv_4; // MC 1.5 port
	}
	
	
	@Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister ir) {
        this.equiv_4 = ir
                .registerIcon("slperiph:pp-0");
        this.equiv_0 = ir
                .registerIcon("slperiph:pp-4");
    }
	
}
