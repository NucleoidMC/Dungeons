package xyz.nucleoid.dungeons.dungeons.game.scripting.enemy_spawn;

import net.minecraft.nbt.NbtCompound;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.TemplateRegion;

@FunctionalInterface
public interface EnemySpawnerBuilder {
    EnemySpawner create(MapTemplate template, TemplateRegion trigger, NbtCompound data, double dungeonLevel) throws ScriptTemplateInstantiationError;
}
