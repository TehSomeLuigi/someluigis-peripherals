package com.someluigi.slperiph.ccportable.shared;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import com.someluigi.slperiph.SLPMod;

public class RecipeQuartzMix implements IRecipe{
	protected ItemStack result = new ItemStack( SLPMod.itemQuartz, 1 );
	
	public boolean matches(InventoryCrafting inv, World world) {
		if ( inv.getSizeInventory() < 9 )
			return false;

		ItemStack center = inv.getStackInSlot(4);
		
		if ( center == null || center.itemID != result.itemID )
			return false;
		
		int freq = center.getItemDamage();
		
		for ( int index = 0; index < inv.getSizeInventory(); index++ ){
			if ( index == 4 ) continue;
			
			ItemStack salt = inv.getStackInSlot(index);
			
			if ( salt != null ){
			
				//Dont allow damageable items, at all!
				if ( salt.getItem().isDamageable() )
					return false;
				
				freq += (( salt.getItemDamage() ^ 37817 ) + salt.itemID * 8) * (index +1);
			}
		}
			
		freq = freq & Short.MAX_VALUE;
	
		result.setItemDamage(freq);
		return true;
	}
	
	public int getRecipeSize() {
		return 9;
	}
	
	public ItemStack getRecipeOutput() {
		return result;
	}
	
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		return result.copy();
	}
}
