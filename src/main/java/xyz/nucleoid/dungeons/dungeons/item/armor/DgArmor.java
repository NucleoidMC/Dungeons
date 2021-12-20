package xyz.nucleoid.dungeons.dungeons.item.armor;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.item.DgAttributeProvider;

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
    double getBaseToughness();
    double getKnockBackResistance();

    @Override
    default Set<EquipmentSlot> getValidSlots(ItemStack stack) {
        return ImmutableSet.of(((ArmorItem)stack.getItem()).getSlotType());
    }


    default void applyArmorAttributes(ItemStack stack) {
        for (EquipmentSlot slot: this.getValidSlots(stack)) {
            UUID modifier_id = ARMOR_MODIFIERS[slot.getEntitySlotId()];
            stack.addAttributeModifier(
                    EntityAttributes.GENERIC_ARMOR,
                    new EntityAttributeModifier(
                            modifier_id,
                            "Armor points modifier",
                            0,
                            EntityAttributeModifier.Operation.MULTIPLY_TOTAL
                    ),
                    slot
            );
            stack.addAttributeModifier(
                    EntityAttributes.GENERIC_ARMOR_TOUGHNESS,
                    new EntityAttributeModifier(
                            modifier_id,
                            "Armor points modifier",
                            this.getBaseToughness(),
                            EntityAttributeModifier.Operation.ADDITION
                    ),
                    slot
            );
            stack.addAttributeModifier(
                    EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE,
                    new EntityAttributeModifier(
                            modifier_id,
                            "Knockback modifier",
                            this.getKnockBackResistance(),
                            EntityAttributeModifier.Operation.ADDITION
                    ),
                    slot
            );
        }
    }
}
