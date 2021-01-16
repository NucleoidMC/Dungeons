package xyz.nucleoid.dungeons.dungeons.item.base;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.Set;

public interface DgAttributeProvider {
    Set<EquipmentSlot> getValidSlots(ItemStack stack);
}
