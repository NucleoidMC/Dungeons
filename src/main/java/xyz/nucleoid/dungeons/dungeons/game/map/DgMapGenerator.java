package xyz.nucleoid.dungeons.dungeons.game.map;

import xyz.nucleoid.plasmid.game.map.template.MapTemplate;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

import java.util.concurrent.CompletableFuture;

public class DgMapGenerator {

    private final DgMapConfig config;

    public DgMapGenerator(DgMapConfig config) {
        this.config = config;
    }

    public CompletableFuture<DgMap> create() {
        return CompletableFuture.supplyAsync(this::build, Util.getMainWorkerExecutor());
    }

    private DgMap build() {
        MapTemplate template = MapTemplate.createEmpty();
        DgMap map = new DgMap(template, this.config);

        this.buildSpawn(template);
        map.spawn = new BlockPos(0,65,0);

        return map;
    }

    private void buildSpawn(MapTemplate builder) {
        BlockPos min = new BlockPos(-5, 64, -5);
        BlockPos max = new BlockPos(5, 64, 5);

        for (BlockPos pos : BlockPos.iterate(min, max)) {
            builder.setBlockState(pos, this.config.spawnBlock);
        }
    }
}
