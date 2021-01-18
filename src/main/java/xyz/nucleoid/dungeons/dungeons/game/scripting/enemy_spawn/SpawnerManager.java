package xyz.nucleoid.dungeons.dungeons.game.scripting.enemy_spawn;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptingUtil;
import xyz.nucleoid.dungeons.dungeons.game.scripting.enemy_spawn.spawners.BasicZombieSpawner;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.map.template.TemplateRegion;
import xyz.nucleoid.plasmid.registry.TinyRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SpawnerManager {
    public static final TinyRegistry<EnemySpawnerBuilder> SPAWNER_BUILDERS = new TinyRegistry<>(Lifecycle.stable());
    private @Nullable List<EnemySpawner> spawnersRunAtStart = new ArrayList<>();

    static {
        register("zombie", BasicZombieSpawner::create);
    }

    private static void register(String id, EnemySpawnerBuilder builder) {
        SPAWNER_BUILDERS.register(new Identifier("dungeons", id), builder);
    }

    public void parseAll(MapTemplate template, double dungeonLevel) throws ScriptTemplateInstantiationError {
        assert this.spawnersRunAtStart != null;

        for (TemplateRegion region : template.getMetadata().getRegions("enemy_spawn").collect(Collectors.toList())) {
            CompoundTag data = region.getData();
            if (data.contains("spawn")) {
                ListTag actionList = data.getList("spawn", NbtType.COMPOUND);
                for (int i = 0; i < actionList.size(); i++) {
                    CompoundTag tag = actionList.getCompound(i);
                    Identifier id = ScriptingUtil.parseDungeonsDefaultId(tag.getString("type"));

                    if (!SPAWNER_BUILDERS.containsKey(id)) {
                        throw new ScriptTemplateInstantiationError("Invalid spawner `" + tag.getString("type") + "`");
                    }

                    EnemySpawner spawner = Objects.requireNonNull(SPAWNER_BUILDERS.get(id)).create(template, region, tag, dungeonLevel);
                    this.spawnersRunAtStart.add(spawner);
                }
            }
        }
    }

    public void spawnAll(ServerWorld world) {
        if (this.spawnersRunAtStart != null) {
            for (EnemySpawner spawner : this.spawnersRunAtStart) {
                spawner.spawn(world);
            }

            this.spawnersRunAtStart = null;
        }
    }
}
