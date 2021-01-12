package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Random;

public enum DgBowType {
    SHORTBOW("shortbow", 5.5),
    LONGBOW("longbow", 6.2),
    CROSSBOW("crossbow", 7);

    public String id;
    public double baseDamage;

    DgBowType(String id, double baseDamage) {
        this.id = id;
        this.baseDamage = baseDamage;
    }

    public static DgBowType choose(Random random) {
        DgBowType[] types = DgBowType.values();
        int idx = random.nextInt(types.length);
        return types[idx];
    }

    public Item asVanillaItem() {
        switch (this) {
            case SHORTBOW:
            case LONGBOW:
                return Items.BOW;
            case CROSSBOW:
                return Items.CROSSBOW;
            default:
                return Items.STICK;
        }
    }
}
