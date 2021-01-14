package xyz.nucleoid.dungeons.dungeons.item.base;

import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.util.DgTranslationUtil;
import xyz.nucleoid.dungeons.dungeons.util.item.DgWeaponStat;

import java.util.List;

public interface DgMeleeWeapon extends DgStatProvider {
    double getMeleeDamage(ItemStack stack);

    double getSwingSpeed(ItemStack stack);

    @Override
    default void appendStats(ItemStack stack, List<DgWeaponStat> stats) {
        stats.add(new DgWeaponStat(getMeleeDamage(stack), DgTranslationUtil.translationKeyOf("stat", "melee_damage")));
        stats.add(new DgWeaponStat(getSwingSpeed(stack), DgTranslationUtil.translationKeyOf("stat", "swing_speed")));
    }
}
