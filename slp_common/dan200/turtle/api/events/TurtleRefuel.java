package dan200.turtle.api.events;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.Cancelable;
import dan200.turtle.api.ITurtleAccess;

@Cancelable
public class TurtleRefuel extends TurtleEvent {
    public ItemStack itemstack;
    public int refuelAmount;
    private boolean handled = false;

    public TurtleRefuel(ITurtleAccess turtle, ItemStack itemstack,
            int fuelToGive) {
        super(turtle);
        this.itemstack = itemstack;
        this.refuelAmount = fuelToGive;
    }

    public boolean isHandled() {
        return this.handled;
    }

    public void setHandled() {
        this.handled = true;
    }
}
