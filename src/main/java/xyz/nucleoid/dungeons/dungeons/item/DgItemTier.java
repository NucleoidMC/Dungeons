package xyz.nucleoid.dungeons.dungeons.item;

import net.minecraft.util.Rarity;

public enum DgItemTier {
    ONE(0xFFFFFF, Rarity.COMMON),
    TWO(0x76BAFF, Rarity.UNCOMMON),
    THREE(0x81FF81, Rarity.RARE),
    FOUR(0xC35BFF, Rarity.EPIC);
    private final int color;
    private final Rarity rarity;

    DgItemTier(int color, Rarity rarity) {
        this.color = color;
        this.rarity = rarity;
    }

    public int getColor() {
        return this.color;
    }

    public Rarity getRarity() {
        return this.rarity;
    }
}
