package xyz.nucleoid.dungeons.dungeons.loot;

import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.item.items.DgItems;
import xyz.nucleoid.dungeons.dungeons.item.DgMaterialItem;
import xyz.nucleoid.dungeons.dungeons.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.item.material.DgMaterial;

import java.util.Random;

public enum DgWeaponLoot {
    RANGED(2, new DgMaterialItem[] { DgItems.SHORTBOW, DgItems.LONGBOW, DgItems.CROSSBOW }),
    METAL_MELEE(9.5, new DgMaterialItem[] { DgItems.HAND_AXE, DgItems.BATTLE_AXE, DgItems.BROADSWORD, DgItems.GREAT_SWORD, DgItems.DAGGER, DgItems.MORNING_STAR }),
    QUARTERSTAFF(Double.MAX_VALUE, new DgMaterialItem[] { DgItems.QUARTERSTAFF });

    private final double randomCutoff;
    private final Generator generator;

    DgWeaponLoot(double randomCutoff, Generator generator) {
        this.randomCutoff = randomCutoff;
        this.generator = generator;
    }

    <T extends Enum<T> & DgMaterial> DgWeaponLoot(double randomCutoff, DgMaterialItem<T>[] items) {
        this(randomCutoff, new MaterialMeleeGenerator<>(items));
    }

    public static ItemStack generate(Random random, double dungeonLevel) {
        double meanLevel = dungeonLevel - 0.5;
        double rand = Math.round(random.nextFloat() * 10.0);

        for (DgWeaponLoot value : values()) {
            if (rand <= value.randomCutoff) {
                return value.generator.generate(random, meanLevel);
            }
        }
        return ItemStack.EMPTY;
    }

    interface Generator {
        ItemStack generate(Random random, double meanLevel);
    }

    record MaterialMeleeGenerator<M extends Enum<M> & DgMaterial>(DgMaterialItem<M>[] items) implements Generator {
        @Override
        public ItemStack generate(Random random, double meanLevel) {
            DgMaterialItem<M> item = this.items[random.nextInt(this.items.length)];
            M material = item.getMaterialComponent().getMaterialPicker().randomlyPick(random, meanLevel);
            return item.createStack(material, DgItemQuality.chooseInRange(random, material.getMinQuality(), material.getMaxQuality()));
        }
    }
}
