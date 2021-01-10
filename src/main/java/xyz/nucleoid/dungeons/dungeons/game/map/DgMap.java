package xyz.nucleoid.dungeons.dungeons.game.map;

import net.minecraft.server.MinecraftServer;

import xyz.nucleoid.dungeons.dungeons.game.map.gen.DgChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class DgMap {
    public DgMap() {
    }

    public ChunkGenerator asGenerator(MinecraftServer server) {
        return new DgChunkGenerator(server);
    }
}
