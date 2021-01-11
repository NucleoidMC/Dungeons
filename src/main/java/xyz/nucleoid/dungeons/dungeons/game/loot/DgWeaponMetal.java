package xyz.nucleoid.dungeons.dungeons.game.loot;

import java.util.Random;

public enum DgWeaponMetal {
    COPPER("Copper", DgLootGrade.BATTERED, DgLootGrade.STURDY),
    BRONZE("Bronze", DgLootGrade.BATTERED, DgLootGrade.FINE),
    IRON("Iron", DgLootGrade.DUSTY, DgLootGrade.FINE),
    STEEL("Steel", DgLootGrade.MEDIOCRE, DgLootGrade.EXCELLENT),
    DAMASCUS("Damascus Steel", DgLootGrade.MEDIOCRE, DgLootGrade.EXCELLENT),
    NETHERITE("Netherite", DgLootGrade.FINE, DgLootGrade.SUPERB),
    VIARITE("Viarite", DgLootGrade.FINE, DgLootGrade.SUPERB),
    GREYSTONE("Greystone", DgLootGrade.FINE, DgLootGrade.SUPERB);

    public final String name;
    // You wouldn't waste netherite on a piece of trash sword
    public final DgLootGrade minGrade;
    // Similarly, you wouldn't use copper for a legendary axe
    public final DgLootGrade maxGrade;

    DgWeaponMetal(String name, DgLootGrade minGrade, DgLootGrade maxGrade) {
        this.name = name;
        this.minGrade = minGrade;
        this.maxGrade = maxGrade;
    }

    public static DgWeaponMetal choose(Random random) {
        DgWeaponMetal[] types = DgWeaponMetal.values();
        int idx = random.nextInt(types.length);
        return types[idx];
    }
}
