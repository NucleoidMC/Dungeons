package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import xyz.nucleoid.dungeons.dungeons.fake.DgItems;

import java.util.Random;

public enum DgRangedWeaponType {
    SHORTBOW("shortbow", 4.6, 15),
    LONGBOW("longbow", 6.2, 20),
    // baseDrawTicks doesn't do anything for crossbow - it breaks client
    CROSSBOW("crossbow", 7, 25);

    public String id;
    public double baseDamage;
    public int baseDrawTicks;

    DgRangedWeaponType(String id, double baseDamage, int baseDrawTicks) {
        this.id = id;
        this.baseDamage = baseDamage;
        this.baseDrawTicks = baseDrawTicks;
    }

    public static DgRangedWeaponType choose(Random random) {
        DgRangedWeaponType[] types = DgRangedWeaponType.values();
        int idx = random.nextInt(types.length);
        return types[idx];
    }

    public Item asItem() {
        switch (this) {
            case SHORTBOW:
            case LONGBOW:
                return DgItems.BOW;
            case CROSSBOW:
                return DgItems.CROSSBOW;
            default:
                return Items.STICK;
        }
    }
}
