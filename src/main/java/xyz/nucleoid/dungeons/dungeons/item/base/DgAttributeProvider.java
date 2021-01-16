package xyz.nucleoid.dungeons.dungeons.item.base;

import com.google.common.collect.Multimap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xyz.nucleoid.dungeons.dungeons.entity.attribute.DgEntityAttributes;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemUtil;

import java.util.Map;
import java.util.Set;

import static net.minecraft.item.ItemStack.MODIFIER_FORMAT;

public interface DgAttributeProvider {
    Set<EquipmentSlot> getValidSlots(ItemStack stack);

    default void applyAttributesText(ItemStack stack) {
        Set<EquipmentSlot> slots = this.getValidSlots(stack);
        for (EquipmentSlot slot : slots) {
            Multimap<EntityAttribute, EntityAttributeModifier> multimap = stack.getAttributeModifiers(slot);
            if (!multimap.isEmpty()) {
                DgItemUtil.addLore(stack, LiteralText.EMPTY);
                if (slots.size() > 1) {
                    DgItemUtil.addLore(stack, new TranslatableText("item.modifiers." + slot.getName()).formatted(Formatting.GRAY));
                }

                for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : multimap.entries()) {
                    EntityAttributeModifier modifier = entry.getValue();
                    double value = modifier.getValue();
                    String translationKey = entry.getKey().getTranslationKey();
                    if (modifier.getId().equals(DgMeleeWeapon.MELEE_DAMAGE_MODIFIER_ID)) {
                        value += 0.5;
                        value += EnchantmentHelper.getAttackDamage(stack, EntityGroup.DEFAULT);
                        translationKey = DgEntityAttributes.translationKeyOf("generic.melee_damage");
                    } else if (modifier.getId().equals(DgMeleeWeapon.SWING_SPEED_MODIFIER_ID)) {
                        value += 4.0;
                        translationKey = DgEntityAttributes.translationKeyOf("generic.swing_speed");
                    }

                    double g;
                    if (modifier.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_BASE && modifier.getOperation() != EntityAttributeModifier.Operation.MULTIPLY_TOTAL) {
                        if (entry.getKey().equals(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)) {
                            g = value * 10.0D;
                        } else {
                            g = value;
                        }
                    } else {
                        g = value * 100.0D;
                    }

                    if (value > 0.0D) {
                        DgItemUtil.addLore(stack, (new LiteralText(" ")).append(new TranslatableText("attribute.modifier.equals." + modifier.getOperation().getId(), MODIFIER_FORMAT.format(g), new TranslatableText(translationKey))).styled(style -> style.withItalic(false)).formatted(Formatting.BLUE));
                    } else if (value < 0.0D) {
                        g *= -1.0D;
                        DgItemUtil.addLore(stack, (new TranslatableText("attribute.modifier.take." + modifier.getOperation().getId(), MODIFIER_FORMAT.format(g), new TranslatableText(translationKey))).styled(style -> style.withItalic(false)).formatted(Formatting.RED));
                    }
                }
            }
        }
    }
}
