package xyz.nucleoid.dungeons.dungeons.game.map;

import xyz.nucleoid.plasmid.game.map.template.MapTemplate;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.CompletableFuture;

public class DgMapGenerator {

    public DgMapGenerator() {
    }

    public CompletableFuture<DgMap> create() {
        return CompletableFuture.supplyAsync(DgMap::new, Util.getMainWorkerExecutor());
    }
}
