package com.someluigi.slperiph.block;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import com.someluigi.slperiph.SLPMod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockSLP extends ItemBlock {

    public ItemBlockSLP(int par1) {
        super(par1);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public String getItemNameIS(ItemStack i) {
        switch (i.getItemDamage()) {
            case 0:
                return "HTTPServer Module";
            default:
                return "Unnamed";
        }
    }

    @Override
    public int getMetadata(int par1) {
        return par1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Icon getIconFromDamage(int par1) {
        return SLPMod.blockSLP.getBlockTextureFromSideAndMetadata(2, par1);
    }

}
