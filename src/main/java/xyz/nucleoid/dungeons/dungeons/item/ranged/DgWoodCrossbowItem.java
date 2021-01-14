package xyz.nucleoid.dungeons.dungeons.item.ranged;

import xyz.nucleoid.dungeons.dungeons.util.item.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgRangedWeaponWood;

public class DgWoodCrossbowItem extends DgMaterialCrossbowItem<DgRangedWeaponWood> {
    public DgWoodCrossbowItem(double baseRangedDamage, int baseDrawSpeed, Settings settings) {
        super(baseRangedDamage, baseDrawSpeed, new DgMaterialComponent<>(DgRangedWeaponWood.class, DgRangedWeaponWood::choose), settings);
    }
}
