package xyz.nucleoid.dungeons.dungeons.game.scripting.quest;


import net.minecraft.nbt.NbtCompound;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.map_templates.MapTemplate;

@FunctionalInterface
public interface QuestObjectiveBuilder {
    QuestObjective create(MapTemplate template, NbtCompound data) throws ScriptTemplateInstantiationError;
}
