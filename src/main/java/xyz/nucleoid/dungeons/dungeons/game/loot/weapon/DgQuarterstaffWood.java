package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import xyz.nucleoid.dungeons.dungeons.game.loot.DgLootGrade;

import java.util.Random;

public enum DgQuarterstaffWood {
    // Tier 1
    CORK("cork", 0.5, DgLootGrade.BATTERED, DgLootGrade.DUSTY),
    BALSA("balsa", 0.7, DgLootGrade.BATTERED, DgLootGrade.DUSTY),

    // Tier 2
    HICKORY("hickory", 1.0, DgLootGrade.MEDIOCRE, DgLootGrade.FINE),
    ASH("ash", 1.1, DgLootGrade.MEDIOCRE, DgLootGrade.FINE),
    YEW("yew", 1.2, DgLootGrade.MEDIOCRE, DgLootGrade.FINE),

    // Tier 3
    EBONY("ebony", 1.4, DgLootGrade.EXCELLENT, DgLootGrade.SUPERB),
    IRONWOOD("ironwood", 1.6, DgLootGrade.EXCELLENT, DgLootGrade.SUPERB),

    // Tier 4
    KIRALIS("kiralis", 1.8, DgLootGrade.LEGENDARY, DgLootGrade.MYTHICAL),
    ARANTEW("arantew", 2.0, DgLootGrade.LEGENDARY, DgLootGrade.MYTHICAL);

    public String id;
    public double damageModifier;
    public final DgLootGrade minGrade;
    public final DgLootGrade maxGrade;

    DgQuarterstaffWood(String id, double damageModifier, DgLootGrade minGrade, DgLootGrade maxGrade) {
        this.id = id;
        this.damageModifier = damageModifier;
        this.minGrade = minGrade;
        this.maxGrade = maxGrade;
    }

    public static DgQuarterstaffWood choose(Random random) {
        DgQuarterstaffWood[] types = DgQuarterstaffWood.values();
        return types[random.nextInt(types.length)];
    }
}
