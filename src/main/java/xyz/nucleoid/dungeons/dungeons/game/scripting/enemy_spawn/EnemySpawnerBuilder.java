package xyz.nucleoid.dungeons.dungeons.game.scripting.enemy_spawn;

import net.minecraft.nbt.CompoundTag;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.map.template.TemplateRegion;

@FunctionalInterface
public interface EnemySpawnerBuilder {
    EnemySpawner create(MapTemplate template, TemplateRegion trigger, CompoundTag data, double dungeonLevel) throws ScriptTemplateInstantiationError;
}
