package dan200.turtle.api.events;

import net.minecraftforge.event.Event;
import dan200.turtle.api.ITurtleAccess;

public class TurtleEvent extends Event {

    public final ITurtleAccess turtle;

    public TurtleEvent(ITurtleAccess turtle) {
        this.turtle = turtle;
    }

}
