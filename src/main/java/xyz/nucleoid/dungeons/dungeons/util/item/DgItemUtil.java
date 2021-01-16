package xyz.nucleoid.dungeons.dungeons.util.item;

import com.google.common.collect.Multimap;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.dungeons.dungeons.entity.attribute.DgEntityAttributes;
import xyz.nucleoid.dungeons.dungeons.item.base.DgAttributeProvider;
import xyz.nucleoid.dungeons.dungeons.item.base.DgFlavorTextProvider;
import xyz.nucleoid.dungeons.dungeons.item.base.DgMeleeWeapon;
import xyz.nucleoid.dungeons.dungeons.item.base.DgRangedWeapon;
import xyz.nucleoid.dungeons.dungeons.util.DgTranslationUtil;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMaterial;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMeleeWeaponMaterial;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgRangedWeaponMaterial;
import xyz.nucleoid.dungeons.dungeons.util.item.model.DgItemModelRegistry;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;

import static net.minecraft.item.ItemStack.MODIFIER_FORMAT;

public class DgItemUtil {
    private static final String QUALITY = "dungeons:quality";
    private static final String MATERIAL = "dungeons:material";
    private static final String MELEE_DAMAGE = "dungeons:melee_damage";
    private static final String SWING_SPEED = "dungeons:swing_speed";
    private static final String RANGED_DAMAGE = "dungeons:ranged_damage";
    private static final String DRAW_TIME = "dungeons:draw_time";

    public static final Random RANDOM = new Random();
    private static final UUID MELEE_DAMAGE_MODIFIER_ID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID SWING_SPEED_MODIFIER_ID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");
    private static final UUID RANGED_DAMAGE_MODIFIER_ID = UUID.fromString("add23882-5767-11eb-ae93-0242ac130002");
    private static final UUID DRAW_TIME_MODIFIER_ID = UUID.fromString("b29017fe-5767-11eb-ae93-0242ac130002");

    public static Identifier idOf(Item item) {
        return Registry.ITEM.getId(item);
    }

    public static String idPathOf(Item item) {
        return idOf(item).getPath();
    }

    public static DgItemQuality qualityOf(ItemStack stack) {
        return DgItemQuality.fromId(stack.getOrCreateTag().getString(QUALITY));
    }

    public static double meleeDamageOf(ItemStack stack) {
        return stack.getOrCreateTag().getDouble(MELEE_DAMAGE);
    }

    public static double swingSpeedOf(ItemStack stack) {
        return stack.getOrCreateTag().getDouble(SWING_SPEED);
    }

    public static double rangedDamageOf(ItemStack stack) {
        return stack.getOrCreateTag().getDouble(RANGED_DAMAGE);
    }

    public static int drawTimeOf(ItemStack stack) {
        return stack.getOrCreateTag().getInt(DRAW_TIME);
    }

    public static <M extends Enum<M> & DgMaterial> M materialOf(ItemStack stack, DgMaterialComponent<M> materialComponent) {
        return materialComponent.getMaterial(stack.getOrCreateTag().getString(MATERIAL));
    }

    public static <M extends Enum<M> & DgMaterial> Text nameOf(ItemStack stack, DgMaterialComponent<M> materialComponent) {
        DgItemQuality quality = DgItemUtil.qualityOf(stack);
        M material = DgItemUtil.materialOf(stack, materialComponent);
        Text materialText = material == null ? new LiteralText("!! Null Material !!").formatted(Formatting.RED) : new TranslatableText(material.getTranslationKey());
        return formatName(new TranslatableText(stack.getItem().getTranslationKey(), materialText), quality);
    }

    public static Text formatName(Text text, DgItemQuality quality) {
        return text.copy().styled(style -> {
            style = style.withItalic(false);
            if (quality != null) {
                style = style.withColor(quality.getTier().getRarity().formatting);
            }
            return style;
        });
    }

    public static void addLore(ItemStack stack, Text text) {
        CompoundTag display = stack.getOrCreateSubTag("display");
        ListTag loreList;
        if (display.contains("Lore", 9)) {
            loreList = display.getList("Lore", 8);
        } else {
            loreList = new ListTag();
            display.put("Lore", loreList);
        }

        loreList.add(StringTag.of(Text.Serializer.toJson(text)));
    }

    public static ItemStackBuilder weaponBuilder(Item item) {
        return ItemStackBuilder.of(item);
    }

    private static void finishWeaponInit(ItemStack stack) {
        Item item = stack.getItem();
        stack.setCustomName(item.getName(stack));
        // https://minecraft.gamepedia.com/Tutorials/Command_NBT_tags#Items
        // hide all, any tooltip info will be added ourselves
        byte flags = 1 + 2 + 4 + 8 + 16 + 32 + 64;
        stack.getOrCreateTag().putByte("HideFlags", flags);
        DgItemQuality quality = qualityOf(stack);
        Text qualityText = quality == null ? new LiteralText("!! Null Quality !!").formatted(Formatting.RED) : new TranslatableText(quality.getTranslationKey()).styled(style -> style.withItalic(false).withColor(TextColor.fromRgb(quality.getTier().getColor())));
        addLore(stack, new TranslatableText(DgTranslationUtil.translationKeyOf("quality", "prefix")).styled(style -> style.withColor(Formatting.GRAY).withItalic(false)).append(qualityText));

        if (item instanceof DgFlavorTextProvider) {
            DgFlavorText flavorText = ((DgFlavorTextProvider) item).getFlavorText(RANDOM, stack);
            int lines = flavorText.getLines();
            for (int i = 1; i <= lines; i++) {
                String translationKey = flavorText.getTranslationKey();
                if (lines != 1) {
                    translationKey = translationKey + "." + i;
                }
                addLore(stack, new TranslatableText(translationKey).styled(style -> style.withItalic(false).withFormatting(Formatting.DARK_GRAY)));
            }
        }

        if (item instanceof DgMeleeWeapon) {
            for (EquipmentSlot slot : ((DgMeleeWeapon) item).getValidSlots(stack)) {
                // Modifier is based on empty hand, so some subtraction must be done
                stack.addAttributeModifier(
                        EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(
                                MELEE_DAMAGE_MODIFIER_ID,
                                "Attack damage modifier",
                                ((DgMeleeWeapon) item).getMeleeDamage(stack) - 0.5, EntityAttributeModifier.Operation.ADDITION
                        ), slot
                );
                stack.addAttributeModifier(
                        EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(
                                SWING_SPEED_MODIFIER_ID, "Attack speed modifier",
                                ((DgMeleeWeapon) item).getSwingSpeed(stack) - 4.0,
                                EntityAttributeModifier.Operation.ADDITION
                        ), slot
                );
            }
        }
        if (item instanceof DgRangedWeapon) {
            for (EquipmentSlot slot : ((DgRangedWeapon) item).getValidSlots(stack)) {
                stack.addAttributeModifier(
                        DgEntityAttributes.GENERIC_RANGED_DAMAGE,
                        new EntityAttributeModifier(
                                RANGED_DAMAGE_MODIFIER_ID,
                                "Ranged damage modifier",
                                ((DgRangedWeapon) item).getRangedDamage(stack), EntityAttributeModifier.Operation.ADDITION
                        ), slot
                );
                stack.addAttributeModifier(
                        DgEntityAttributes.GENERIC_DRAW_TIME,
                        new EntityAttributeModifier(
                                DRAW_TIME_MODIFIER_ID, "Draw time modifier",
                                ((DgRangedWeapon) item).getDrawTime(stack),
                                EntityAttributeModifier.Operation.ADDITION
                        ), slot
                );
            }
        }

        if (item instanceof DgAttributeProvider) {
            Set<EquipmentSlot> slots = ((DgAttributeProvider) item).getValidSlots(stack);
            for (EquipmentSlot slot : slots) {
                Multimap<EntityAttribute, EntityAttributeModifier> multimap = stack.getAttributeModifiers(slot);
                if (!multimap.isEmpty()) {
                    addLore(stack, LiteralText.EMPTY);
                    if (slots.size() > 1) {
                        addLore(stack, new TranslatableText("item.modifiers." + slot.getName()).formatted(Formatting.GRAY));
                    }

                    for (Map.Entry<EntityAttribute, EntityAttributeModifier> entry : multimap.entries()) {
                        EntityAttributeModifier modifier = entry.getValue();
                        double value = modifier.getValue();
                        String translationKey = entry.getKey().getTranslationKey();
                        if (modifier.getId().equals(MELEE_DAMAGE_MODIFIER_ID)) {
                            value += 0.5;
                            value += EnchantmentHelper.getAttackDamage(stack, EntityGroup.DEFAULT);
                            translationKey = DgEntityAttributes.translationKeyOf("generic.melee_damage");
                        } else if (modifier.getId().equals(SWING_SPEED_MODIFIER_ID)) {
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
                            addLore(stack, (new LiteralText(" ")).append(new TranslatableText("attribute.modifier.equals." + modifier.getOperation().getId(), MODIFIER_FORMAT.format(g), new TranslatableText(translationKey))).styled(style -> style.withItalic(false)).formatted(Formatting.BLUE));
                        } else if (value < 0.0D) {
                            g *= -1.0D;
                            addLore(stack, (new TranslatableText("attribute.modifier.take." + modifier.getOperation().getId(), MODIFIER_FORMAT.format(g), new TranslatableText(translationKey))).styled(style -> style.withItalic(false)).formatted(Formatting.RED));
                        }
                    }
                }
            }
        }
    }

    public static ItemStack initWeapon(ItemStack stack, DgItemQuality quality) {
        stack.getOrCreateTag().putString(QUALITY, quality.getId());
        addCustomModel(stack, DgItemUtil.idPathOf(stack.getItem()));
        finishWeaponInit(stack);
        return stack;
    }

    public static <M extends Enum<M> & DgMaterial> ItemStack initMaterialWeapon(ItemStack stack, M material, DgItemQuality quality) {
        stack.getOrCreateTag().putString(QUALITY, quality.getId());
        stack.getOrCreateTag().putString(MATERIAL, material.getId());
        addCustomModel(stack, material.getId(), DgItemUtil.idPathOf(stack.getItem()));
        finishWeaponInit(stack);
        return stack;
    }

    public static <M extends Enum<M> & DgMeleeWeaponMaterial> ItemStack initMeleeMaterialWeapon(ItemStack stack, M material, DgItemQuality quality, double baseMeleeDamage, double baseSwingSpeed) {
        stack.getOrCreateTag().putDouble(MELEE_DAMAGE, (baseMeleeDamage + (RANDOM.nextDouble() / 2)) * material.getMeleeDamageMultiplier() * quality.getDamageMultiplier());
        stack.getOrCreateTag().putDouble(SWING_SPEED, baseSwingSpeed);
        return initMaterialWeapon(stack, material, quality);
    }

    public static <M extends Enum<M> & DgRangedWeaponMaterial> ItemStack initRangedMaterialWeapon(ItemStack stack, M material, DgItemQuality quality, double baseRangedDamage, int baseDrawTime) {
        stack.getOrCreateTag().putDouble(RANGED_DAMAGE, (baseRangedDamage + (RANDOM.nextDouble() / 2)) * material.getRangedDamageMultiplier() * quality.getDamageMultiplier());
        stack.getOrCreateTag().putInt(DRAW_TIME, baseDrawTime);
        return initMaterialWeapon(stack, material, quality);
    }

    public static void addCustomModel(ItemStack stack, String... modifiers) {
        stack.getOrCreateTag().putInt("CustomModelData", DgItemModelRegistry.getId(modifiers));
    }

    public static <M extends Enum<M> & DgMaterial> void appendMaterialStacks(ItemGroup group, DefaultedList<ItemStack> stacks, DgMaterialComponent<M> materialComponent, BiFunction<M, DgItemQuality, ItemStack> stackFactory) {
        for (DgItemQuality quality : DgItemQuality.values()) {
            if (group == quality.getItemGroup()) {
                for (M material : materialComponent.getMaterials()) {
                    if (quality.ordinal() >= material.getMinQuality().ordinal() && quality.ordinal() <= material.getMaxQuality().ordinal()) {
                        stacks.add(stackFactory.apply(material, quality));
                    }
                }
            }
        }
    }
}
