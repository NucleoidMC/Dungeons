package xyz.nucleoid.dungeons.dungeons.game.scripting.behavior;

import net.minecraft.block.Block;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.util.BlockBounds;

import java.util.List;

public class ExplodableRegion {
    public final BlockBounds region;
    private final @Nullable List<Block> affectedBlocks;

    public ExplodableRegion(BlockBounds region, @Nullable List<Block> affectedBlocks) {
        this.region = region;
        this.affectedBlocks = affectedBlocks;
    }

    public boolean isExplodable(Block block) {
        if (this.affectedBlocks != null) {
            return this.affectedBlocks.contains(block);
        } else {
            return true;
        }
    }
}
