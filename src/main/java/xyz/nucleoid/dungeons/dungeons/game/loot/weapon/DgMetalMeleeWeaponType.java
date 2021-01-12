package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Random;

public enum DgMetalMeleeWeaponType {
    HAND_AXE("hand_axe", 6),
    BATTLE_AXE("battle_axe", 7),
    SWORD("sword", 5),
    GREAT_SWORD("great_sword", 7),
    DAGGER("dagger", 2.5),
    MACE("mace", 4),
    // TODO(restioson): how to handle these having different materials?
//    QUARTERSTAFF("Quarterstaff"),
//    BOW("Bow"),
//    CROSSBOW("Crossbow");
    ;

    public final String id;
    public final double baseDamage;

    DgMetalMeleeWeaponType(String id, double baseDamage) {
        this.id = id;
        this.baseDamage = baseDamage;
    }

    public static DgMetalMeleeWeaponType choose(Random random) {
        DgMetalMeleeWeaponType[] types = DgMetalMeleeWeaponType.values();
        int idx = random.nextInt(types.length);
        return types[idx];
    }

    public Item asVanillaItem() {
        switch(this) {
            case BATTLE_AXE:
            case HAND_AXE:
                return Items.IRON_AXE;
            case SWORD:
            case GREAT_SWORD:
            case DAGGER:
                return Items.IRON_SWORD;
            case MACE:
                return Items.IRON_SHOVEL;
            default:
                return Items.STICK;
        }
    }
}
