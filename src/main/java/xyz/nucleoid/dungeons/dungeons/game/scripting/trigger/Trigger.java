package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger;

import xyz.nucleoid.map_templates.BlockBounds;

import java.util.List;

public record Trigger(BlockBounds region,
                      TriggerCriterion criterion,
                      List<Action> actions) {
}
