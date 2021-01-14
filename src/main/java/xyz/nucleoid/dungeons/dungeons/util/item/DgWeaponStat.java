package xyz.nucleoid.dungeons.dungeons.util.item;

public class DgWeaponStat {
    private final double value;
    private final String translationKey;

    public DgWeaponStat(double value, String translationKey) {
        this.value = value;
        this.translationKey = translationKey;
    }

    public double getValue() {
        return value;
    }

    public String getTranslationKey() {
        return translationKey;
    }
}
