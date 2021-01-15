package xyz.nucleoid.dungeons.dungeons.util.item.loot;

import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.item.DgItems;
import xyz.nucleoid.dungeons.dungeons.item.base.DgMaterialItem;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMaterial;

import java.util.Random;

public enum DgWeaponLoot {
    RANGED(2, DgItems.SHORTBOW, DgItems.LONGBOW, DgItems.CROSSBOW),
    METAL_MELEE(9.5, DgItems.HAND_AXE, DgItems.BATTLE_AXE, DgItems.BROADSWORD, DgItems.GREAT_SWORD, DgItems.DAGGER, DgItems.MORNING_STAR),
    QUARTERSTAFF(Double.MAX_VALUE, DgItems.QUARTERSTAFF);

    private final double randomCutoff;
    private final Generator generator;

    DgWeaponLoot(double randomCutoff, Generator generator) {
        this.randomCutoff = randomCutoff;
        this.generator = generator;
    }

    DgWeaponLoot(double randomCutoff, DgMaterialItem<? extends DgMaterial>... items) {
        this(randomCutoff, new MaterialMeleeGenerator(items));
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

    static class MaterialMeleeGenerator<M extends Enum<M> & DgMaterial> implements Generator {
        private final DgMaterialItem<M>[] items;

        MaterialMeleeGenerator(DgMaterialItem<M>... items) {
            this.items = items;
        }

        @Override
        public ItemStack generate(Random random, double meanLevel) {
            DgMaterialItem<M> item = items[random.nextInt(items.length)];
            M material = item.getMaterialComponent().getMaterialPicker().randomlyPick(random, meanLevel);
            return item.createStack(material, DgItemQuality.chooseInRange(random, material.getMinQuality(), material.getMaxQuality()));
        }
    }
}
