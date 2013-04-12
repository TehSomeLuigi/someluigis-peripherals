package com.someluigi.slperiph.ccportable.shared;


import java.awt.Color;
import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import com.someluigi.slperiph.SLPMod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computer.api.ComputerCraftAPI;

public class ItemQuartz extends Item {

	public static ItemStack newCrystal( int freq ){
		ItemStack stack = new ItemStack( SLPMod.itemQuartz, 1 );
			stack.setItemDamage(freq);
			
		return stack;
	}
	
	public static ItemStack copyCrystal( ItemStack item ){
		return newCrystal( item.getItemDamage() );
	}
	
	public ItemQuartz(int id) {
		super(id);
	
		setCreativeTab(ComputerCraftAPI.getCreativeTab());
		
		setUnlocalizedName("slp.pp.ItemQuartz");
		
		setHasSubtypes(true);
		setNoRepair();
	}
	
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack item, int pass) {
		int damage = item.getItemDamage();
		
		if ( damage == 0 )
			return 0xFFFFFF;
		
		return Color.HSBtoRGB( damage / 32767f, 0.8f, 0.8f );
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })

	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack item, EntityPlayer player, List data, boolean isExtended) {
		data.add( "Frequency: " + item.getItemDamage() );
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamage(int par1)
    {
        return this.iconIndex;
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void updateIcons(IconRegister ir)
    {
        this.iconIndex =  ir
                .registerIcon("slperiph:ppitem-quartz");
    }
	
}
