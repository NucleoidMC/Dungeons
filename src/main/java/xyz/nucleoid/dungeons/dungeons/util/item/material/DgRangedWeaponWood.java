package xyz.nucleoid.dungeons.dungeons.util.item.material;

import xyz.nucleoid.dungeons.dungeons.util.item.DgItemQuality;

import java.util.Random;

// FIXME(restioson): I don't know enough about bows so I just came up with these on the spot
// Can someone take a look?
public enum DgRangedWeaponWood implements DgWeaponMaterial {
    // Tier 1
    PINE("pine", 0.7, DgItemQuality.BATTERED, DgItemQuality.DUSTY),
    BALSA("balsa", 0.8, DgItemQuality.BATTERED, DgItemQuality.DUSTY),

    // Tier 2
    OAK("oak", 1.0, DgItemQuality.MEDIOCRE, DgItemQuality.FINE),
    CEDAR("cedar", 1.2, DgItemQuality.MEDIOCRE, DgItemQuality.FINE),

    // Tier 3
    HORN("horn", 1.3, DgItemQuality.EXCELLENT, DgItemQuality.SUPERB),
    ASH("ash", 1.5, DgItemQuality.EXCELLENT, DgItemQuality.SUPERB),
    YEW("yew", 1.6, DgItemQuality.EXCELLENT, DgItemQuality.SUPERB),

    // Tier 4
    KIRALIS("kiralis", 1.8, DgItemQuality.LEGENDARY, DgItemQuality.MYTHICAL),
    ERELIAN("erelian", 2.2, DgItemQuality.LEGENDARY, DgItemQuality.MYTHICAL);

    public final String id;
    public double damageMultiplier;
    public DgItemQuality minQuality;
    public DgItemQuality maxQuality;

    DgRangedWeaponWood(String id, double damageMultiplier, DgItemQuality minQuality, DgItemQuality maxQuality) {
        this.id = id;
        this.damageMultiplier = damageMultiplier;
        this.minQuality = minQuality;
        this.maxQuality = maxQuality;
    }

    public static DgRangedWeaponWood choose(Random random, double meanLevel) {
        double number = random.nextGaussian() * 0.8 + meanLevel;
        int ord = (int) Math.round(number);

        DgRangedWeaponWood[] types = DgRangedWeaponWood.values();
        return types[Math.max(0, Math.min(types.length, ord))];
    }

    @Override
    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public DgItemQuality getMinQuality() {
        return minQuality;
    }

    @Override
    public DgItemQuality getMaxQuality() {
        return maxQuality;
    }
}
