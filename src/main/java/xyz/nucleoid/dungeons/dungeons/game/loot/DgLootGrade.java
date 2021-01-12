package xyz.nucleoid.dungeons.dungeons.game.loot;

import java.util.Random;

public enum DgLootGrade {
    // Tier 1
    BATTERED("battered", 1.0, 0xD2D2D2),
    OLD("old", 1.1, 0xE4E4E4),
    DUSTY("dusty", 1.2, 0xFFFFFF),

    // Tier 2
    MEDIOCRE("mediocre", 1.0, 0xFFCFD4),
    STURDY("sturdy", 1.1, 0xFFFDCF),
    FINE("fine", 1.2, 0xCFE3FF),

    // Tier 3
    EXCELLENT("excellent", 1.1, 0x81FF81),
    SUPERB("superb", 1.2, 0xFFC66B),

    // Tier 4
    LEGENDARY("legendary", 1.0, 0xC35BFF),
    MYTHICAL("mythical", 1.1, 0x61FFFD);

    public String id;
    public double damageModifier;
    public int color;

    DgLootGrade(String id, double damageModifier, int color) {
        this.id = id;
        this.damageModifier = damageModifier;
        this.color = color;
    }

    public static DgLootGrade chooseInRange(Random random, DgLootGrade min, DgLootGrade max) {
        DgLootGrade[] types = DgLootGrade.values();
        int idx = random.nextInt(max.ordinal() - min.ordinal() + 1) + min.ordinal(); // inclusive between min and max
        return types[idx];
    }
}
