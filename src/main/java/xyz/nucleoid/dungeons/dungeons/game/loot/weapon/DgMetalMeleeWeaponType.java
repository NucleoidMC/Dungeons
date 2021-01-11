package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Random;

public enum DgMetalMeleeWeaponType {
    HAND_AXE("Handaxe", 6),
    BATTLE_AXE("Battleaxe", 7),
    SWORD("Sword", 5),
    GREAT_SWORD("Greatsword", 7),
    DAGGER("Dagger", 2.5),
    MACE("Mace", 4),
    // TODO(restioson): how to handle these having different materials?
//    QUARTERSTAFF("Quarterstaff"),
//    BOW("Bow"),
//    CROSSBOW("Crossbow");
    ;

    public final String name;
    public final double baseDamage;

    DgMetalMeleeWeaponType(String name, double baseDamage) {
        this.name = name;
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
