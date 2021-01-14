package xyz.nucleoid.dungeons.dungeons.item.ranged;

import xyz.nucleoid.dungeons.dungeons.util.item.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgRangedWeaponWood;

public class DgWoodBowItem extends DgMaterialBowItem<DgRangedWeaponWood> {
    public DgWoodBowItem(double baseRangedDamage, int baseDrawTime, Settings settings) {
        super(baseRangedDamage, baseDrawTime, new DgMaterialComponent<>(DgRangedWeaponWood.class, DgRangedWeaponWood::choose), settings);
    }
}
