package com.someluigi.slperiph.ccportable.shared;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.someluigi.slperiph.SLPMod;

public class SlotCrystal extends Slot{

	public static boolean isValid( ItemStack item ){
		return item.itemID == SLPMod.itemQuartz.itemID && item.getItemDamage() != 0;
	}
	
	public SlotCrystal(IInventory inv, int index, int x, int y) {
		super(inv, index, x, y);
	}

	public boolean isItemValid(ItemStack item) {
		return isValid(item);
	}

	public int getSlotStackLimit() {
		return 1;
	}

}
