package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import xyz.nucleoid.dungeons.dungeons.game.loot.DgLootGrade;

import java.util.Random;

public enum DgQuarterstaffWood {
    // Tier 1
    CORK("Cork", 0.5, DgLootGrade.BATTERED, DgLootGrade.DUSTY),
    BALSA("Balsa", 0.7, DgLootGrade.BATTERED, DgLootGrade.DUSTY),

    // Tier 2
    HICKORY("Hickory", 1.0, DgLootGrade.MEDIOCRE, DgLootGrade.FINE),
    ASH("Ash", 1.1, DgLootGrade.MEDIOCRE, DgLootGrade.FINE),
    YEW("Yew", 1.2, DgLootGrade.MEDIOCRE, DgLootGrade.FINE),

    // Tier 3
    EBONY("Ebony", 1.4, DgLootGrade.EXCELLENT, DgLootGrade.SUPERB),
    IRONWOOD("Ironwood", 1.6, DgLootGrade.EXCELLENT, DgLootGrade.SUPERB),

    // Tier 4
    KIRALIS("Kiralis", 1.8, DgLootGrade.LEGENDARY, DgLootGrade.MYTHICAL),
    ARANTEW("Arantew", 2.0, DgLootGrade.LEGENDARY, DgLootGrade.MYTHICAL);

    public String name;
    public double damageModifier;
    public final DgLootGrade minGrade;
    public final DgLootGrade maxGrade;

    DgQuarterstaffWood(String name, double damageModifier, DgLootGrade minGrade, DgLootGrade maxGrade) {
        this.name = name;
        this.damageModifier = damageModifier;
        this.minGrade = minGrade;
        this.maxGrade = maxGrade;
    }

    public static DgQuarterstaffWood choose(Random random) {
        DgQuarterstaffWood[] types = DgQuarterstaffWood.values();
        return types[random.nextInt(types.length)];
    }
}
