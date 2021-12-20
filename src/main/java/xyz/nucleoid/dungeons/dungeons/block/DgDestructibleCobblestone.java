package xyz.nucleoid.dungeons.dungeons.block;

import eu.pb4.polymer.api.block.PolymerBlock;
import net.minecraft.block.*;

public class DgDestructibleCobblestone extends Block implements PolymerBlock {
    public DgDestructibleCobblestone() {
        super(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(2.0F, 2.0F));
    }

    @Override
    public Block getPolymerBlock(BlockState state) {
        // there is no block state data to store or send
        return Blocks.INFESTED_COBBLESTONE;
    }
}
