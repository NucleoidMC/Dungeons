package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Random;

public enum DgMetalMeleeWeaponType {
    HAND_AXE("Handaxe", 6, 1.1),
    BATTLE_AXE("Battleaxe", 7, 0.8),
    SWORD("Sword", 5, 1.6),
    GREAT_SWORD("Greatsword", 7, 0.8),
    DAGGER("Dagger", 2.5, 3.2),
    MACE("Mace", 4.5, 1.5);

    public final String name;
    public final double baseDamage;
    public final double baseAttackSpeed;

    DgMetalMeleeWeaponType(String name, double baseDamage, double baseAttackSpeed) {
        this.name = name;
        this.baseDamage = baseDamage;
        this.baseAttackSpeed = baseAttackSpeed;
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
