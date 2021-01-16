package xyz.nucleoid.dungeons.dungeons.item.base;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.Set;

public interface DgRangedWeapon extends DgAttributeProvider {
    double getRangedDamage(ItemStack stack);

    int getDrawTime(ItemStack stack);

    @Override
    default Set<EquipmentSlot> getValidSlots(ItemStack stack) {
        return ImmutableSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
    }
}
