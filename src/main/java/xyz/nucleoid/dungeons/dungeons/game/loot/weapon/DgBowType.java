package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Random;

public enum DgBowType {
    SHORTBOW("Shortbow", 5.5),
    LONGBOW("Longbow", 6.2),
    CROSSBOW("Crossbow", 7);

    public String name;
    public double baseDamage;

    DgBowType(String name, double baseDamage) {
        this.name = name;
        this.baseDamage = baseDamage;
    }

    public static DgBowType choose(Random random) {
        DgBowType[] types = DgBowType.values();
        int idx = random.nextInt(types.length);
        return types[idx];
    }

    public Item asVanillaItem() {
        switch(this) {
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
