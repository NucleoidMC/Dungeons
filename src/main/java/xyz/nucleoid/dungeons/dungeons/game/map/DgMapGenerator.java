package xyz.nucleoid.dungeons.dungeons.game.map;

import net.minecraft.util.math.BlockPos;
import xyz.nucleoid.map_templates.MapTemplate;

public class DgMapGenerator {

    private final DgMapConfig config;

    public DgMapGenerator(DgMapConfig config) {
        this.config = config;
    }

    public DgMap build() {
        MapTemplate template = MapTemplate.createEmpty();
        this.buildSpawn(template);
        return new DgMap(template);
    }

    private void buildSpawn(MapTemplate builder) {
        BlockPos min = new BlockPos(-5, 64, -5);
        BlockPos max = new BlockPos(5, 64, 5);

        for (BlockPos pos : BlockPos.iterate(min, max)) {
            builder.setBlockState(pos, this.config.spawnBlock);
        }
    }
}
