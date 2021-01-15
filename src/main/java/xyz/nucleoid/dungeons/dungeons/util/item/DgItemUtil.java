package xyz.nucleoid.dungeons.dungeons.util.item;

import net.minecraft.entity.EquipmentSlot;
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
import xyz.nucleoid.dungeons.dungeons.item.base.DgFlavorTextProvider;
import xyz.nucleoid.dungeons.dungeons.item.base.DgStatProvider;
import xyz.nucleoid.dungeons.dungeons.util.DgTranslationUtil;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMaterial;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMeleeWeaponMaterial;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgRangedWeaponMaterial;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.BiFunction;

public class DgItemUtil {
    private static final String QUALITY = "dungeons:quality";
    private static final String MATERIAL = "dungeons:material";
    private static final String MELEE_DAMAGE = "dungeons:melee_damage";
    private static final String SWING_SPEED = "dungeons:swing_speed";
    private static final String RANGED_DAMAGE = "dungeons:ranged_damage";
    private static final String DRAW_TIME = "dungeons:draw_time";

    public static final Random RANDOM = new Random();
    private static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("da3f4761-b0ef-4fdb-a55f-b4269c804055");
    private static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("ad949c2b-e696-4e50-b98e-325978db25f9");

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

    private static void finishWeaponTooltip(ItemStack stack) {
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

        if (item instanceof DgStatProvider) {
            List<DgWeaponStat> stats = new ArrayList<>();
            ((DgStatProvider) item).appendStats(stack, stats);
            if (!stats.isEmpty()) {
                addLore(stack, new LiteralText(""));
            }
            for (DgWeaponStat stat : stats) {
                DecimalFormat format = new DecimalFormat("0.#");
                double value = stat.getValue();
                addLore(stack, new TranslatableText(stat.getTranslationKey(), (value >= 0 ? "+" : "") + format.format(value)).styled(style -> style.withItalic(false).withColor(Formatting.BLUE)));
            }
        }
    }

    public static ItemStack initWeapon(ItemStack stack, DgItemQuality quality) {
        stack.getOrCreateTag().putString(QUALITY, quality.getId());
        addCustomModel(stack, DgItemUtil.idPathOf(stack.getItem()));
        finishWeaponTooltip(stack);
        return stack;
    }

    public static <M extends Enum<M> & DgMaterial> ItemStack initMaterialWeapon(ItemStack stack, M material, DgItemQuality quality) {
        stack.getOrCreateTag().putString(QUALITY, quality.getId());
        stack.getOrCreateTag().putString(MATERIAL, material.getId());
        addCustomModel(stack, material.getId(), DgItemUtil.idPathOf(stack.getItem()));
        finishWeaponTooltip(stack);
        return stack;
    }

    public static <M extends Enum<M> & DgMeleeWeaponMaterial> ItemStack initMeleeMaterialWeapon(ItemStack stack, M material, DgItemQuality quality, double baseMeleeDamage, double baseSwingSpeed) {
        stack.getOrCreateTag().putDouble(MELEE_DAMAGE, (baseMeleeDamage + (RANDOM.nextDouble() / 2)) * material.getMeleeDamageMultiplier() * quality.getDamageMultiplier());
        stack.getOrCreateTag().putDouble(SWING_SPEED, baseSwingSpeed);

        // Modifier is based on empty hand, so some subtraction must be done
        stack.addAttributeModifier(
                EntityAttributes.GENERIC_ATTACK_DAMAGE, 
                new EntityAttributeModifier(
                        ATTACK_DAMAGE_MODIFIER_ID,
                        "Attack damage modifier",
                        baseMeleeDamage - 0.5, EntityAttributeModifier.Operation.ADDITION
                ),
                EquipmentSlot.MAINHAND
        );
        stack.addAttributeModifier(
                EntityAttributes.GENERIC_ATTACK_SPEED, 
                new EntityAttributeModifier(
                        ATTACK_SPEED_MODIFIER_ID, "Attack speed modifier",
                        baseSwingSpeed - 4.0,
                        EntityAttributeModifier.Operation.ADDITION
                ), EquipmentSlot.MAINHAND
        );

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
