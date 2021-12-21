package xyz.nucleoid.dungeons.dungeons.item.weapon.melee;

import net.minecraft.item.Item;
import xyz.nucleoid.dungeons.dungeons.item.material.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.item.material.DgMeleeWeaponMetal;

public class DgMorningStarItem extends DgMetalMeleeWeaponItem {
    public DgMorningStarItem(double baseAttackDamage, double baseAttackSpeed, Item proxy, Settings settings) {
        super(baseAttackDamage, baseAttackSpeed, proxy, settings);
        this.materialComponent = new DgMaterialComponent<>(DgMeleeWeaponMetal.class, (random, meanLevel) -> DgMeleeWeaponMetal.choose(random, meanLevel, true));
    }
}
