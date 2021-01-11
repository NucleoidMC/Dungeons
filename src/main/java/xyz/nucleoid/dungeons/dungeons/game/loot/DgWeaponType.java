package xyz.nucleoid.dungeons.dungeons.game.loot;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Random;

public enum DgWeaponType {
    HAND_AXE("Hand axe"),
    BATTLE_AXE("Battleaxe"),
    SWORD("Sword"),
    GREAT_SWORD("Greatsword"),
    DAGGER("Dagger"),
    MACE("Mace"),
    // TODO(restioson): how to handle these having different materials?
//    QUARTERSTAFF("Quarterstaff"),
//    BOW("Bow"),
//    CROSSBOW("Crossbow");
    ;

    DgWeaponType(String name) {
        this.name = name;
    }

    public final String name;

    public static DgWeaponType choose(Random random) {
        DgWeaponType[] types = DgWeaponType.values();
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
