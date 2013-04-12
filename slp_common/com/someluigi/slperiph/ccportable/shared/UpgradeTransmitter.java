package com.someluigi.slperiph.ccportable.shared;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

import com.someluigi.slperiph.SLPMod;

import dan200.computer.api.IHostedPeripheral;
import dan200.turtle.api.ITurtleAccess;
import dan200.turtle.api.ITurtleUpgrade;
import dan200.turtle.api.TurtleSide;
import dan200.turtle.api.TurtleUpgradeType;
import dan200.turtle.api.TurtleVerb;

public class UpgradeTransmitter implements ITurtleUpgrade{
    
    public static Icon tuIcon;
    
	public int getUpgradeID() {
		return 175;
	}

	//Generic
	public String getAdjective() {
		return "Terminal";
	}

	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	//Crafting
	public ItemStack getCraftingItem() {
		return new ItemStack( SLPMod.blockTransmitter );
	}

	public boolean isSecret() {
		return false;
	}
	
	/*
	//Rendering
	public String getIconTexture(ITurtleAccess turtle, TurtleSide side) {
		return CommonProxy.TEX_BLOCKS;
	}

	public int getIconIndex(ITurtleAccess turtle, TurtleSide side) {
		return 3;
	}
	*/

	//Peripheral
	public IHostedPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return new PeripheralTerminal(turtle);
	}

	public boolean useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb action, int dir) {
		return false;
	}

    @Override
    public Icon getIcon(ITurtleAccess turtle, TurtleSide side) {
        return SLPMod.blockTransmitter.turtle_equiv_3;
    }

}
