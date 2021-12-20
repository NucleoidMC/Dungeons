package xyz.nucleoid.dungeons.dungeons.game.map;

import net.minecraft.server.MinecraftServer;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.plasmid.game.world.generator.TemplateChunkGenerator;

public class DgMap {
    private final MapTemplate template;
    public BlockPos spawn;

    public DgMap(MapTemplate template) {
        this.template = template;
    }

    public ChunkGenerator asGenerator(MinecraftServer server) {
        return new TemplateChunkGenerator(server, this.template);
    }
}
