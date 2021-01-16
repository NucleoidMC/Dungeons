package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger;

import net.minecraft.nbt.CompoundTag;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.map.template.TemplateRegion;

@FunctionalInterface
public interface ActionBuilder {
    Action create(MapTemplate template, TemplateRegion trigger, CompoundTag data) throws TriggerInstantiationError;
}
