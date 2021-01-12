package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Random;

public enum DgMetalMeleeWeaponType {
    HAND_AXE("hand_axe", 6, 1.1),
    BATTLE_AXE("battle_axe", 7, 0.8),
    SWORD("sword", 5, 1.6),
    GREAT_SWORD("great_sword", 7, 0.8),
    DAGGER("dagger", 2.5, 3.2),
    MACE("mace", 4.5, 1.5);

    public final String id;
    public final double baseDamage;
    public final double baseAttackSpeed;

    DgMetalMeleeWeaponType(String id, double baseDamage, double baseAttackSpeed) {
        this.id = id;
        this.baseDamage = baseDamage;
        this.baseAttackSpeed = baseAttackSpeed;
    }

    public static DgMetalMeleeWeaponType choose(Random random) {
        DgMetalMeleeWeaponType[] types = DgMetalMeleeWeaponType.values();
        int idx = random.nextInt(types.length);
        return types[idx];
    }

    public Item asItem() {
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
