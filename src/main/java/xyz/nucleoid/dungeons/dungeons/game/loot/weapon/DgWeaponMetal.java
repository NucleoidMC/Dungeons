package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.game.loot.DgLootGrade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public enum DgWeaponMetal {
    // Tier 1
    COPPER("Copper", 0.9, DgLootGrade.BATTERED, DgLootGrade.DUSTY),
    BRONZE("Bronze", 1.0, DgLootGrade.BATTERED, DgLootGrade.DUSTY),

    // Tier 2
    IRON("Iron", 1.2, DgLootGrade.MEDIOCRE, DgLootGrade.FINE),
    STEEL("Steel", 1.4, DgLootGrade.MEDIOCRE, DgLootGrade.FINE),
    DAMASCUS("Damascus Steel", 1.6, DgLootGrade.MEDIOCRE, DgLootGrade.FINE),

    // Tier 3
    NETHERITE("Netherite", 1.8, DgLootGrade.EXCELLENT, DgLootGrade.MYTHICAL),
    VIARITE("Viarite", 2.0, DgLootGrade.EXCELLENT, DgLootGrade.MYTHICAL),
    GREYSTONE("Greystone", 2.2, DgLootGrade.EXCELLENT, DgLootGrade.MYTHICAL);

    public final String name;
    public final double damageModifier;
    // You wouldn't waste netherite on a piece of trash sword
    public final DgLootGrade minGrade;
    // Similarly, you wouldn't use copper for a legendary axe
    public final DgLootGrade maxGrade;

    DgWeaponMetal(String name, double damageModifier, DgLootGrade minGrade, DgLootGrade maxGrade) {
        this.name = name;
        this.damageModifier = damageModifier;
        this.minGrade = minGrade;
        this.maxGrade = maxGrade;
    }

    public static DgWeaponMetal choose(Random random, @Nullable DgWeaponMetal skip) {
        if (skip == null) {
            DgWeaponMetal[] types = DgWeaponMetal.values();
            return types[random.nextInt(types.length)];
        } else {
            ArrayList<DgWeaponMetal> types = new ArrayList<>();
            Collections.addAll(types, DgWeaponMetal.values());
            types.remove(skip.ordinal());
            return types.get(random.nextInt(types.size()));
        }
    }
}
