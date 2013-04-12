package com.someluigi.slperiph.ccportable.shared;


import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computer.api.ComputerCraftAPI;

public class ItemPDA extends Item {

	public ItemPDA(int id) {
		super(id);
	
		setCreativeTab(ComputerCraftAPI.getCreativeTab());
		
		setUnlocalizedName("slp.pp.ItemPDA");
		
		setMaxStackSize(1);
		setNoRepair();
	}
	
	public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player) {
	
		if ( !world.isRemote )
			GuiManager.openGui(player, GuiManager.GUI_TERMINAL);
		
		return item;
	}
	
	public int getMaxItemUseDuration(ItemStack par1ItemStack) {
		return 1;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack item, EntityPlayer player, List info, boolean isAdvanced1) {
		
		if ( item.hasTagCompound() ){
			NBTTagCompound data = item.getTagCompound();
			
			info.add( "Crystals: " + data.getInteger( "crystalCount" ) );
		}
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
                .registerIcon("slperiph:ppitem-handheld");
    }
	
}
