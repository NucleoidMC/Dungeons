package xyz.nucleoid.dungeons.dungeons.item.weapon.ranged;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.entity.attribute.DgEntityAttributes;
import xyz.nucleoid.dungeons.dungeons.item.DgAttributeProvider;

import java.util.Set;
import java.util.UUID;

public interface DgRangedWeapon extends DgAttributeProvider {
    UUID RANGED_DAMAGE_MODIFIER_ID = UUID.fromString("add23882-5767-11eb-ae93-0242ac130002");
    UUID DRAW_TIME_MODIFIER_ID = UUID.fromString("b29017fe-5767-11eb-ae93-0242ac130002");

    double getRangedDamage(ItemStack stack);

    int getDrawTime(ItemStack stack);

    @Override
    default Set<EquipmentSlot> getValidSlots(ItemStack stack) {
        return ImmutableSet.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
    }

    default void applyRangedWeaponAttributes(ItemStack stack) {
        for (EquipmentSlot slot : this.getValidSlots(stack)) {
            stack.addAttributeModifier(
                    DgEntityAttributes.GENERIC_RANGED_DAMAGE,
                    new EntityAttributeModifier(
                            RANGED_DAMAGE_MODIFIER_ID,
                            "Ranged damage modifier",
                            this.getRangedDamage(stack), EntityAttributeModifier.Operation.ADDITION
                    ), slot
            );
            stack.addAttributeModifier(
                    DgEntityAttributes.GENERIC_DRAW_TIME,
                    new EntityAttributeModifier(
                            DRAW_TIME_MODIFIER_ID, "Draw time modifier",
                            this.getDrawTime(stack),
                            EntityAttributeModifier.Operation.ADDITION
                    ), slot
            );
        }
    }
}
