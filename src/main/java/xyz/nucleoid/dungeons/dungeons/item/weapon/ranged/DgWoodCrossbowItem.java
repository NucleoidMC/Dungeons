package xyz.nucleoid.dungeons.dungeons.item.weapon.ranged;

import xyz.nucleoid.dungeons.dungeons.item.material.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.item.material.DgRangedWeaponWood;

public class DgWoodCrossbowItem extends DgMaterialCrossbowItem<DgRangedWeaponWood> {
    public DgWoodCrossbowItem(double baseRangedDamage, int baseDrawSpeed, Settings settings) {
        super(baseRangedDamage, baseDrawSpeed, new DgMaterialComponent<>(DgRangedWeaponWood.class, DgRangedWeaponWood::choose), settings);
    }
}
