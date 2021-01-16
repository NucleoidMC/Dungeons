package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger;
import xyz.nucleoid.plasmid.util.BlockBounds;

import java.util.List;

public class Trigger {
    public final BlockBounds region;
    public final TriggerCriterion criterion;
    public final List<Action> actions;

    public Trigger(BlockBounds region, TriggerCriterion criterion, List<Action> actions) {
        this.region = region;
        this.criterion = criterion;
        this.actions = actions;
    }
}
