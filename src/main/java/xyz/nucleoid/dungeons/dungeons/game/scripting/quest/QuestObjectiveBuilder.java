package xyz.nucleoid.dungeons.dungeons.game.scripting.quest;


import net.minecraft.nbt.CompoundTag;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.plasmid.map.template.MapTemplate;

@FunctionalInterface
public interface QuestObjectiveBuilder {
    QuestObjective create(MapTemplate template, CompoundTag data) throws ScriptTemplateInstantiationError;
}
