package xyz.nucleoid.dungeons.dungeons.item.material;

import xyz.nucleoid.dungeons.dungeons.item.DgItemQuality;

import java.util.Random;

public enum DgQuarterstaffWood implements DgWeaponMaterial {
    // Tier 1
    CORK("cork", 0.5, DgItemQuality.BATTERED, DgItemQuality.DUSTY),
    BALSA("balsa", 0.7, DgItemQuality.BATTERED, DgItemQuality.DUSTY),

    // Tier 2
    HICKORY("hickory", 1.0, DgItemQuality.MEDIOCRE, DgItemQuality.FINE),
    ASH("ash", 1.1, DgItemQuality.MEDIOCRE, DgItemQuality.FINE),
    YEW("yew", 1.2, DgItemQuality.MEDIOCRE, DgItemQuality.FINE),

    // Tier 3
    EBONY("ebony", 1.4, DgItemQuality.EXCELLENT, DgItemQuality.SUPERB),
    IRONWOOD("ironwood", 1.6, DgItemQuality.EXCELLENT, DgItemQuality.SUPERB),

    // Tier 4
    KIRALIS("kiralis", 1.8, DgItemQuality.LEGENDARY, DgItemQuality.MYTHICAL),
    ARANTEW("arantew", 2.0, DgItemQuality.LEGENDARY, DgItemQuality.MYTHICAL);

    public String id;
    public double damageMultiplier;
    public final DgItemQuality minQuality;
    public final DgItemQuality maxQuality;

    DgQuarterstaffWood(String id, double damageMultiplier, DgItemQuality minQuality, DgItemQuality maxQuality) {
        this.id = id;
        this.damageMultiplier = damageMultiplier;
        this.minQuality = minQuality;
        this.maxQuality = maxQuality;
    }

    public static DgQuarterstaffWood choose(Random random, double meanLevel) {
        double number = random.nextGaussian() * 0.8 + meanLevel;
        int ord = (int) Math.round(number);

        DgQuarterstaffWood[] types = DgQuarterstaffWood.values();
        return types[Math.max(0, Math.min(types.length - 1, ord))];
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public DgItemQuality getMinQuality() {
        return this.minQuality;
    }

    @Override
    public DgItemQuality getMaxQuality() {
        return this.maxQuality;
    }

    @Override
    public double getDamageMultiplier() {
        return this.damageMultiplier;
    }
}
