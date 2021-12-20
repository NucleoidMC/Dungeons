package xyz.nucleoid.dungeons.dungeons.util.item.loot;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.collection.WeightedList;
import net.minecraft.util.collection.Weighting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// TODO(restioson): use loot tables? how do they work?
public enum DgConsumable {
    INSTANT_HEALTH(3),
    GOLDEN_APPLE(1);

    public int weight;

    public final static List<Weighted.Present<DgConsumable>> LIST = new ArrayList<>();

    static {
        for (DgConsumable value : DgConsumable.values()) {
            LIST.add(Weighted.of(value, value.weight));
        }
    }

    DgConsumable(int weight) {
        this.weight = weight;
    }

    public static DgConsumable choose(Random random) {
        return Weighting.getRandom(random, LIST).orElseThrow().getData();
    }

    public ItemStack asItemStack() {
        return switch (this) {
            case INSTANT_HEALTH -> PotionUtil.setPotion(Items.POTION.getDefaultStack(), Potions.HEALING);
            case GOLDEN_APPLE -> new ItemStack(Items.GOLDEN_APPLE);
        };
    }
}