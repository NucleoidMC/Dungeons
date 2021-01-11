package xyz.nucleoid.dungeons.dungeons.game.loot;

import java.util.Random;

public enum DgLootGrade {
    // Tier 1
    BATTERED("Battered", 1.0),
    OLD("Old", 1.1),
    DUSTY("Dusty", 1.2),

    // Tier 2
    MEDIOCRE("Mediocre", 1.0),
    STURDY("Sturdy", 1.1),
    FINE("Fine", 1.2),

    // Tier 3
    EXCELLENT("Excellent", 1.1),
    SUPERB("Superb", 1.2),

    // Tier 4
    LEGENDARY("Legendary", 1.0),
    MYTHICAL("Mythical", 1.1);

    public String name;
    public double damageModifier;

    DgLootGrade(String name, double damageModifier) {
        this.name = name;
        this.damageModifier = damageModifier;
    }

    public static DgLootGrade chooseInRange(Random random, DgLootGrade min, DgLootGrade max) {
        DgLootGrade[] types = DgLootGrade.values();
        int idx = random.nextInt(max.ordinal() - min.ordinal() + 1) + min.ordinal(); // inclusive between min and max
        return types[idx];
    }
}
