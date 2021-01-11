package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import xyz.nucleoid.dungeons.dungeons.game.loot.DgLootGrade;

import java.util.Random;

// FIXME(restioson): I don't know enough about bows so I just came up with these on the spot
// Can someone take a look?
public enum DgBowMaterial {
    // Tier 1
    PINE("Pine", 0.7, DgLootGrade.BATTERED, DgLootGrade.DUSTY),
    BALSA("Balsa", 0.8, DgLootGrade.BATTERED, DgLootGrade.DUSTY),

    // Tier 2
    OAK("Oak", 1.0, DgLootGrade.MEDIOCRE, DgLootGrade.FINE),
    CEDAR("Cedar", 1.2, DgLootGrade.MEDIOCRE, DgLootGrade.FINE),

    // Tier 3
    HORN("Horn", 1.3, DgLootGrade.EXCELLENT, DgLootGrade.SUPERB),
    ASH("Ash", 1.5, DgLootGrade.EXCELLENT, DgLootGrade.SUPERB),
    YEW("Yew", 1.6, DgLootGrade.EXCELLENT, DgLootGrade.SUPERB),

    // Tier 4
    KIRALIS("Kiralis", 1.8, DgLootGrade.LEGENDARY, DgLootGrade.MYTHICAL),
    ERELIAN("Erelian", 2.2, DgLootGrade.LEGENDARY, DgLootGrade.MYTHICAL);

    public final String name;
    public double damageModifier;
    public DgLootGrade minGrade;
    public DgLootGrade maxGrade;

    DgBowMaterial(String name, double damageModifier, DgLootGrade minGrade, DgLootGrade maxGrade) {
        this.name = name;
        this.damageModifier = damageModifier;
        this.minGrade = minGrade;
        this.maxGrade = maxGrade;
    }

    public static DgBowMaterial choose(Random random) {
        DgBowMaterial[] types = DgBowMaterial.values();
        int idx = random.nextInt(types.length);
        return types[idx];
    }
}
