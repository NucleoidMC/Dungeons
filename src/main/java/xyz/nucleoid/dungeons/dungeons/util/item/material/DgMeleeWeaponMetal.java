package xyz.nucleoid.dungeons.dungeons.util.item.material;

import xyz.nucleoid.dungeons.dungeons.util.item.DgItemQuality;

import java.util.Random;

public enum DgMeleeWeaponMetal implements DgMeleeWeaponMaterial {
    // Tier 1,
    COPPER("copper", 0.9, DgItemQuality.BATTERED, DgItemQuality.DUSTY),
    BRONZE("bronze", 1.0, DgItemQuality.BATTERED, DgItemQuality.DUSTY),

    // Tier 2
    IRON("iron", 1.2, DgItemQuality.MEDIOCRE, DgItemQuality.FINE),
    STEEL("steel", 1.4, DgItemQuality.MEDIOCRE, DgItemQuality.FINE),
    DAMASCUS_STEEL("damascus_steel", 1.6, DgItemQuality.MEDIOCRE, DgItemQuality.FINE),

    // Tier 3
    NETHERITE("netherite", 1.8, DgItemQuality.EXCELLENT, DgItemQuality.MYTHICAL),
    VIARITE("viarite", 2.0, DgItemQuality.EXCELLENT, DgItemQuality.MYTHICAL),
    GREYSTONE("greystone", 2.2, DgItemQuality.EXCELLENT, DgItemQuality.MYTHICAL);

    public final String id;
    public final double meleeDamageMultiplier;
    // You wouldn't waste netherite on a piece of trash sword
    public final DgItemQuality minQuality;
    // Similarly, you wouldn't use copper for a legendary axe
    public final DgItemQuality maxQuality;

    DgMeleeWeaponMetal(String id, double meleeDamageMultiplier, DgItemQuality minQuality, DgItemQuality maxQuality) {
        this.id = id;
        this.meleeDamageMultiplier = meleeDamageMultiplier;
        this.minQuality = minQuality;
        this.maxQuality = maxQuality;
    }

    public static DgMeleeWeaponMetal choose(Random random, double meanLevel, boolean replaceDamascus) {
        double number = random.nextGaussian() * 0.8 + meanLevel;
        int ord = (int) Math.round(number);

        DgMeleeWeaponMetal[] types = DgMeleeWeaponMetal.values();
        DgMeleeWeaponMetal type = types[Math.max(0, Math.min(types.length, ord))];

        if (replaceDamascus && type == DgMeleeWeaponMetal.DAMASCUS_STEEL) {
            type = DgMeleeWeaponMetal.STEEL;
        }

        return type;
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

    @Override
    public double getMeleeDamageMultiplier() {
        return meleeDamageMultiplier;
    }
}
