package xyz.nucleoid.dungeons.dungeons.game.map;

import net.minecraft.server.MinecraftServer;
import xyz.nucleoid.plasmid.game.map.template.MapTemplate;
import xyz.nucleoid.plasmid.game.map.template.TemplateChunkGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class DgMap {
    private final MapTemplate template;
    private final DgMapConfig config;
    public BlockPos spawn;

    public DgMap(MapTemplate template, DgMapConfig config) {
        this.template = template;
        this.config = config;
    }

    public ChunkGenerator asGenerator(MinecraftServer server) {
        return new TemplateChunkGenerator(server, this.template, BlockPos.ORIGIN);
    }
}
