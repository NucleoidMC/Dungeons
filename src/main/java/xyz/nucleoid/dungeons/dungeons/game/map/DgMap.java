package xyz.nucleoid.dungeons.dungeons.game.map;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import xyz.nucleoid.dungeons.dungeons.game.DgConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.dungeons.dungeons.game.scripting.behavior.ExplodableRegion;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.Action;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.TriggerManager;
import xyz.nucleoid.map_templates.*;
import xyz.nucleoid.plasmid.game.GameOpenException;
import xyz.nucleoid.plasmid.game.world.generator.TemplateChunkGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DgMap {
    public final BlockBounds spawn;
    public final List<Action> spawnActions;
    public final float spawnAngle;
    public final MapTemplate template;
    public List<ExplodableRegion> explodableRegions;
    public List<BlockBounds> fallDamageReduceRegions;

    private DgMap(BlockBounds spawn, List<Action> spawnActions, float spawnAngle, MapTemplate template) {
        this.spawn = spawn;
        this.spawnActions = spawnActions;
        this.spawnAngle = spawnAngle;
        this.template = template;
        this.explodableRegions = new ArrayList<>();
        this.fallDamageReduceRegions = new ArrayList<>();
    }

    public static DgMap create(MinecraftServer server, DgConfig config) throws GameOpenException {
        try {
            return DgMap.fromTemplate(server, MapTemplateSerializer.loadFromResource(server, config.map));
        } catch (IOException e) {
            throw new GameOpenException(new LiteralText("Failed to load map"), e);
        }
    }

    private static DgMap fromTemplate(MinecraftServer server, MapTemplate template) throws GameOpenException {
        MapTemplateMetadata meta = template.getMetadata();
        NbtCompound globalData = meta.getData();

        RegistryKey<Biome> biome = BiomeKeys.FOREST;
        if (globalData.contains("biome")) {
            biome = RegistryKey.of(Registry.BIOME_KEY, new Identifier(globalData.getString("biome")));
        }

        if (biome.getValue().toString().equalsIgnoreCase("minecraft:dark_forest_hills")) {
            biome = RegistryKey.of(Registry.BIOME_KEY, new Identifier("minecraft:dark_forest"));
        }

        if (!server.getRegistryManager().get(Registry.BIOME_KEY).contains(biome)) {
            throw new GameOpenException(new LiteralText("No such biome " + biome.toString() + " exists"));
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

        DgMap map = new DgMap(spawnRegion.getBounds(), spawnActions, spawnRegion.getData().getFloat("yaw"), template);

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

    public ChunkGenerator asGenerator(MinecraftServer server) {
        return new TemplateChunkGenerator(server, this.template);
    }
}
