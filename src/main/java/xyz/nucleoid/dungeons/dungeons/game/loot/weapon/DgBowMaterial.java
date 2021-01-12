package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import xyz.nucleoid.dungeons.dungeons.game.loot.DgLootGrade;

import java.util.Random;

// FIXME(restioson): I don't know enough about bows so I just came up with these on the spot
// Can someone take a look?
public enum DgBowMaterial {
    // Tier 1
    PINE("pine", 0.7, DgLootGrade.BATTERED, DgLootGrade.DUSTY),
    BALSA("balsa", 0.8, DgLootGrade.BATTERED, DgLootGrade.DUSTY),

    // Tier 2
    OAK("oak", 1.0, DgLootGrade.MEDIOCRE, DgLootGrade.FINE),
    CEDAR("cedar", 1.2, DgLootGrade.MEDIOCRE, DgLootGrade.FINE),

    // Tier 3
    HORN("horn", 1.3, DgLootGrade.EXCELLENT, DgLootGrade.SUPERB),
    ASH("ash", 1.5, DgLootGrade.EXCELLENT, DgLootGrade.SUPERB),
    YEW("yew", 1.6, DgLootGrade.EXCELLENT, DgLootGrade.SUPERB),

    // Tier 4
    KIRALIS("kiralis", 1.8, DgLootGrade.LEGENDARY, DgLootGrade.MYTHICAL),
    ERELIAN("erelian", 2.2, DgLootGrade.LEGENDARY, DgLootGrade.MYTHICAL);

    public final String id;
    public double damageModifier;
    public DgLootGrade minGrade;
    public DgLootGrade maxGrade;

    DgBowMaterial(String id, double damageModifier, DgLootGrade minGrade, DgLootGrade maxGrade) {
        this.id = id;
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
