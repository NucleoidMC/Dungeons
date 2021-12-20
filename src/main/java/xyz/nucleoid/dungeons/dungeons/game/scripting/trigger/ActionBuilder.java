package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger;

import net.minecraft.nbt.NbtCompound;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.TemplateRegion;

@FunctionalInterface
public interface ActionBuilder {
    Action create(MapTemplate template, TemplateRegion trigger, NbtCompound data) throws ScriptTemplateInstantiationError;
}
