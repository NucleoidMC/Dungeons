package xyz.nucleoid.dungeons.dungeons.item.base;

import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.util.item.DgWeaponStat;

import java.util.List;

public interface DgStatProvider {
    void appendStats(ItemStack stack, List<DgWeaponStat> stats);
}
