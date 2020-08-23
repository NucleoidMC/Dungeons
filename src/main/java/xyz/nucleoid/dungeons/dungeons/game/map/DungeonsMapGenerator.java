package xyz.nucleoid.dungeons.dungeons.game.map;

import xyz.nucleoid.plasmid.game.map.template.MapTemplate;
import xyz.nucleoid.plasmid.util.BlockBounds;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.dungeons.dungeons.game.DungeonsConfig;

import java.util.concurrent.CompletableFuture;

public class DungeonsMapGenerator {

    private final DungeonsMapConfig config;

    public DungeonsMapGenerator(DungeonsMapConfig config) {
        this.config = config;
    }

    public CompletableFuture<DungeonsMap> create() {
        return CompletableFuture.supplyAsync(this::build, Util.getMainWorkerExecutor());
    }

    private DungeonsMap build() {
        MapTemplate template = MapTemplate.createEmpty();
        DungeonsMap map = new DungeonsMap(template, this.config);

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
