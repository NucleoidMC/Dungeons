package xyz.nucleoid.dungeons.dungeons.game.map;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;

import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import xyz.nucleoid.dungeons.dungeons.game.DgConfig;
import xyz.nucleoid.dungeons.dungeons.game.map.gen.DgChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.dungeons.dungeons.game.map.gen.DgProcgenMapConfig;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.dungeons.dungeons.game.scripting.behavior.ExplodableRegion;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.Action;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.TriggerManager;
import xyz.nucleoid.plasmid.game.GameOpenException;
import xyz.nucleoid.plasmid.map.template.*;
import xyz.nucleoid.plasmid.util.BlockBounds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DgMap {
    public final BlockBounds spawn;
    public final List<Action> spawnActions;
    public final float spawnAngle;
    public final Either<MapTemplate, DgProcgenMapConfig> templateOrGenerator;
    public List<ExplodableRegion> explodableRegions;
    public List<BlockBounds> fallDamageReduceRegions;

    private DgMap(BlockBounds spawn, List<Action> spawnActions, float spawnAngle, Either<MapTemplate, DgProcgenMapConfig> templateOrGenerator) {
        this.spawn = spawn;
        this.spawnActions = spawnActions;
        this.spawnAngle = spawnAngle;
        this.templateOrGenerator = templateOrGenerator;
        this.explodableRegions = new ArrayList<>();
        this.fallDamageReduceRegions = new ArrayList<>();
    }

    public static DgMap create(DgConfig config) throws GameOpenException {
        Either<MapTemplate, DgProcgenMapConfig> either = config.map.mapLeft(id -> {
            try {
                return MapTemplateSerializer.INSTANCE.loadFromResource(id);
            } catch (IOException e) {
                throw new GameOpenException(new LiteralText("Failed to load map"), e);
            }
        });

        Optional<DgMap> opt = either.left().map(DgMap::fromTemplate);

        if (!opt.isPresent()) {
            opt = either.right().map(DgMap::fromGenerator);
            assert opt.isPresent();
        }
        return opt.get();
    }

    private static DgMap fromTemplate(MapTemplate template) throws GameOpenException {
        MapTemplateMetadata meta = template.getMetadata();
        CompoundTag globalData = meta.getData();

        RegistryKey<Biome> biome = BiomeKeys.FOREST;
        if (globalData.contains("biome")) {
            biome = RegistryKey.of(Registry.BIOME_KEY, new Identifier(globalData.getString("biome")));
        }
        template.setBiome(biome);

        TemplateRegion spawnRegion = meta.getFirstRegion("spawn");
        if (spawnRegion == null) {
            throw new GameOpenException(new LiteralText("No `spawn` region is present but it is required"));
        }

        List<Action> spawnActions;
        try {
            spawnActions = TriggerManager.parseActions(template, spawnRegion);
        } catch (ScriptTemplateInstantiationError e) {
            throw new GameOpenException(new LiteralText(e.reason));
        }

        DgMap map = new DgMap(spawnRegion.getBounds(), spawnActions, spawnRegion.getData().getFloat("yaw"), Either.left(template));

        List<TemplateRegion> rawExplodableRegions = meta.getRegions("explodable").collect(Collectors.toList());
        map.explodableRegions = new ArrayList<>();

        for (TemplateRegion region : rawExplodableRegions) {
            try {
                map.explodableRegions.add(ExplodableRegion.parse(region));
            } catch (ScriptTemplateInstantiationError e) {
                throw new GameOpenException(new LiteralText(e.reason));
            }
        }

        List<TemplateRegion> rawReplacementRegions = meta.getRegions("replace_blocks").collect(Collectors.toList());

        for (TemplateRegion region : rawReplacementRegions) {
            try {
                BlockReplacementRegion blockReplacementRegion = BlockReplacementRegion.parse(region);
                blockReplacementRegion.replaceAll(template);
            } catch (ScriptTemplateInstantiationError e) {
                throw new GameOpenException(new LiteralText(e.reason));
            }
        }

        map.fallDamageReduceRegions = meta.getRegionBounds("fall_damage_reduced").collect(Collectors.toList());

        return map;
    }

    private static DgMap fromGenerator(DgProcgenMapConfig config) {
        return new DgMap(BlockBounds.of(new BlockPos(0, 40, 0)), ImmutableList.of(), 0, Either.right(config));
    }

    public ChunkGenerator asGenerator(MinecraftServer server) {
        Optional<ChunkGenerator> opt = this.templateOrGenerator.left().map(t -> new TemplateChunkGenerator(server, t));

        if (!opt.isPresent()) {
            opt = this.templateOrGenerator.right().map(cfg -> new DgChunkGenerator(server));
            assert opt.isPresent();
        }
        return opt.get();
    }
}
