package xyz.nucleoid.dungeons.dungeons.block;

import net.minecraft.block.*;
import xyz.nucleoid.plasmid.fake.FakeBlock;

public class DgDestructibleCobblestone extends Block implements FakeBlock {
    public DgDestructibleCobblestone() {
        super(AbstractBlock.Settings.of(Material.STONE).requiresTool().strength(2.0F, 2.0F));
    }

    @Override
    public Block asProxy() {
        return Blocks.INFESTED_COBBLESTONE;
    }

    @Override
    public BlockState asProxy(BlockState state) {
        // there is no block state data to store or send
        return this.asProxy().getDefaultState();
    }
}
