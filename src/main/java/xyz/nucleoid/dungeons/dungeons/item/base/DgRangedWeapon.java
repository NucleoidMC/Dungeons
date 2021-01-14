package xyz.nucleoid.dungeons.dungeons.item.base;

import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.util.DgTranslationUtil;
import xyz.nucleoid.dungeons.dungeons.util.item.DgWeaponStat;

import java.util.List;

public interface DgRangedWeapon extends DgStatProvider {
    double getRangedDamage(ItemStack stack);

    int getDrawTime(ItemStack stack);

    @Override
    default void appendStats(ItemStack stack, List<DgWeaponStat> stats) {
        stats.add(new DgWeaponStat(getRangedDamage(stack), DgTranslationUtil.translationKeyOf("stat", "ranged_damage")));
        stats.add(new DgWeaponStat(getDrawTime(stack) / 20.0D, DgTranslationUtil.translationKeyOf("stat", "draw_time")));
    }
}
