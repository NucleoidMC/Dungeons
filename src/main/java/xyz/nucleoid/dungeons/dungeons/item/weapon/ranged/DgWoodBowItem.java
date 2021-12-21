package xyz.nucleoid.dungeons.dungeons.item.weapon.ranged;

import xyz.nucleoid.dungeons.dungeons.item.material.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.item.material.DgRangedWeaponWood;

public class DgWoodBowItem extends DgMaterialBowItem<DgRangedWeaponWood> {
    public DgWoodBowItem(double baseRangedDamage, int baseDrawTime, Settings settings) {
        super(baseRangedDamage, baseDrawTime, new DgMaterialComponent<>(DgRangedWeaponWood.class, DgRangedWeaponWood::choose), settings);
    }
}
