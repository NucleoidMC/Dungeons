package xyz.nucleoid.dungeons.dungeons.item.base;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;

import java.util.Set;
import java.util.UUID;

/**
 * DgArmor is a utility class used for armors.
 * @apiNote Armor Points are not present in this implementation, as this seems to only be cosmetic, and armor points
 * could be used to display a custom status bar.
 */
public interface DgArmor extends DgAttributeProvider {
    // Here are the modifiers used by minecraft for each slot of the armor (Boots, Leggings, Chestplate, Helmet)
    UUID[] ARMOR_MODIFIERS = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    double getArmorToughness();
    double getKnockBackResistance();

    @Override
    default Set<EquipmentSlot> getValidSlots(ItemStack stack) {
        // Check for the type of the item
        if (!(stack.getItem() instanceof ArmorItem)) {
            return ImmutableSet.of(EquipmentSlot.MAINHAND);
        }
        return ImmutableSet.of(((ArmorItem)stack.getItem()).getSlotType());
    }
}
