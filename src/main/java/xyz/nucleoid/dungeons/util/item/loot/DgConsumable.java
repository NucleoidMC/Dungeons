package xyz.nucleoid.dungeons.dungeons.util.item.loot;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.collection.WeightedList;

import java.util.Random;

// TODO(restioson): use loot tables? how do they work?
public enum DgConsumable {
    INSTANT_HEALTH(5),
    GOLDEN_APPLE(1);

    public final static WeightedList<DgConsumable> LIST = new WeightedList<>();

    public int weight;

    DgConsumable(int weight) {
        this.weight = weight;
    }

    static void lazyGenerateList() {
        if (LIST.isEmpty()) {
            for (DgConsumable value : DgConsumable.values()) {
                LIST.add(value, value.weight);
            }
        }
    }

    public static DgConsumable choose(Random random) {
        DgConsumable.lazyGenerateList();
        return LIST.pickRandom(random);
    }

    public ItemStack asItemStack() {
        switch (this) {
            case INSTANT_HEALTH:
                return PotionUtil.setPotion(Items.POTION.getDefaultStack(), Potions.HEALING);
            case GOLDEN_APPLE:
                return new ItemStack(Items.GOLDEN_APPLE);
            default:
                return new ItemStack(Items.STICK);
        }
    }
}