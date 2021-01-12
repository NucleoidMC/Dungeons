package xyz.nucleoid.dungeons.dungeons.game.loot;

import java.util.Random;

public enum DgLootGrade {
    // Tier 1
    BATTERED("battered", 1.0),
    OLD("old", 1.1),
    DUSTY("dusty", 1.2),

    // Tier 2
    MEDIOCRE("mediocre", 1.0),
    STURDY("sturdy", 1.1),
    FINE("fine", 1.2),

    // Tier 3
    EXCELLENT("excellent", 1.1),
    SUPERB("superb", 1.2),

    // Tier 4
    LEGENDARY("legendary", 1.0),
    MYTHICAL("mythical", 1.1);

    public String id;
    public double damageModifier;

    DgLootGrade(String id, double damageModifier) {
        this.id = id;
        this.damageModifier = damageModifier;
    }

    public static DgLootGrade chooseInRange(Random random, DgLootGrade min, DgLootGrade max) {
        DgLootGrade[] types = DgLootGrade.values();
        int idx = random.nextInt(max.ordinal() - min.ordinal() + 1) + min.ordinal(); // inclusive between min and max
        return types[idx];
    }
}
