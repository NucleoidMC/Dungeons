package xyz.nucleoid.dungeons.dungeons.item.melee;

import net.minecraft.item.Item;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMeleeWeaponMetal;

public class DgMorningStarItem extends DgMetalMeleeWeaponItem {
    public DgMorningStarItem(double baseAttackDamage, double baseAttackSpeed, Item proxy, Settings settings) {
        super(baseAttackDamage, baseAttackSpeed, proxy, settings);
        this.materialComponent = new DgMaterialComponent<>(DgMeleeWeaponMetal.class, (random, meanLevel) -> DgMeleeWeaponMetal.choose(random, meanLevel, true));
    }
}
