package xyz.nucleoid.dungeons.dungeons.game.loot;

import org.apache.commons.lang3.RandomUtils;

import java.util.Random;

public enum DgLootGrade {
    BATTERED("Battered"),
    OLD("Old"),
    DUSTY("Dusty"),
    MEDIOCRE(""),
    STURDY("Sturdy"),
    FINE("Fine"),
    EXCELLENT("Excellent"),
    SUPERB("Superb"),
    LEGENDARY("Legendary"),
    MYTHICAL("Mythical");

    public String name;

    DgLootGrade(String name) {
        this.name = name;
    }

    public static DgLootGrade chooseInRange(Random random, DgLootGrade min, DgLootGrade max) {
        DgLootGrade[] types = DgLootGrade.values();
        int idx = random.nextInt(max.ordinal() - min.ordinal() + 1) + min.ordinal(); // inclusive between min and max
        return types[idx];
    }
}
