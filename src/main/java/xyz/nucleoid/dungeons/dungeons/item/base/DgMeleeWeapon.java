package xyz.nucleoid.dungeons.dungeons.item.base;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;

import java.util.Set;
import java.util.UUID;

public interface DgMeleeWeapon extends DgAttributeProvider {
    UUID MELEE_DAMAGE_MODIFIER_ID = UUID.fromString("cb3f55d3-645c-4f38-a497-9c13a33db5cf");
    UUID SWING_SPEED_MODIFIER_ID = UUID.fromString("fa233e1c-4180-4865-b01b-bcce9785aca3");

    double getMeleeDamage(ItemStack stack);

    double getSwingSpeed(ItemStack stack);

    @Override
    default Set<EquipmentSlot> getValidSlots(ItemStack stack) {
        return ImmutableSet.of(EquipmentSlot.MAINHAND);
    }

    default void applyMeleeWeaponAttributes(ItemStack stack) {
        for (EquipmentSlot slot : this.getValidSlots(stack)) {
            // Modifier is based on empty hand, so some subtraction must be done
            stack.addAttributeModifier(
                    EntityAttributes.GENERIC_ATTACK_DAMAGE,
                    new EntityAttributeModifier(
                            MELEE_DAMAGE_MODIFIER_ID,
                            "Attack damage modifier",
                            this.getMeleeDamage(stack) - 0.5, EntityAttributeModifier.Operation.ADDITION
                    ), slot
            );
            stack.addAttributeModifier(
                    EntityAttributes.GENERIC_ATTACK_SPEED,
                    new EntityAttributeModifier(
                            SWING_SPEED_MODIFIER_ID, "Attack speed modifier",
                            this.getSwingSpeed(stack) - 4.0,
                            EntityAttributeModifier.Operation.ADDITION
                    ), slot
            );
        }
    }
}
