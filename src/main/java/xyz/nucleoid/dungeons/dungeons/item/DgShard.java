package xyz.nucleoid.dungeons.dungeons.item;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import xyz.nucleoid.plasmid.fake.FakeItem;

public class DgShard extends Item implements FakeItem {
    public DgShard(Settings settings) {
        super(settings);
    }

    @Override
    public Item asProxy() {
        return Items.PRISMARINE_SHARD;
    }
}
