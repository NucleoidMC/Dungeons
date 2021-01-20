package xyz.nucleoid.dungeons.dungeons.game.scripting.enemy_spawn.spawners;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import xyz.nucleoid.dungeons.dungeons.entity.enemy.DgZombieEntity;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptingUtil;
import xyz.nucleoid.dungeons.dungeons.game.scripting.enemy_spawn.EnemySpawner;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.map.template.TemplateRegion;
import xyz.nucleoid.plasmid.util.BlockBounds;

public class BasicZombieSpawner implements EnemySpawner {
    private final int count;
    private final double level;
    private final BlockBounds targetRegion;

    public BasicZombieSpawner(int count, double level, BlockBounds targetRegion) {
        this.count = count;
        this.level = level;
        this.targetRegion = targetRegion;
    }

    public static EnemySpawner create(MapTemplate template, TemplateRegion trigger, CompoundTag data, double dungeonLevel) throws ScriptTemplateInstantiationError {
        int count = 1;
        if (data.contains("count")) {
            count = data.getInt("count");
        }

        double level_above_dungeon = 0.0;
        if (data.contains("level_above_dungeon")) {
            level_above_dungeon = data.getDouble("level_above_dungeon");
        }

        BlockBounds target = ScriptingUtil.getTargetOrDefault(template, trigger, data);
        return new BasicZombieSpawner(count, dungeonLevel + level_above_dungeon, target);
    }

    @Override
    public void spawn(ServerWorld world) {
        for (int i = 0; i < this.count; i++) {
            DgZombieEntity zombie = new DgZombieEntity(world);
            Vec3d pos = ScriptingUtil.pickRandomBottomCoord(this.targetRegion, world.getRandom());
            ScriptingUtil.loadChunkAndSpawn(world, zombie, pos);
        }
    }
}
