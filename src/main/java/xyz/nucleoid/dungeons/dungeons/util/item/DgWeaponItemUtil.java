package xyz.nucleoid.dungeons.dungeons.util.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xyz.nucleoid.dungeons.dungeons.item.base.DgAttributeProvider;
import xyz.nucleoid.dungeons.dungeons.item.base.DgFlavorTextProvider;
import xyz.nucleoid.dungeons.dungeons.item.base.DgMeleeWeapon;
import xyz.nucleoid.dungeons.dungeons.item.base.DgRangedWeapon;
import xyz.nucleoid.dungeons.dungeons.util.DgTranslationUtil;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMaterial;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMeleeWeaponMaterial;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgRangedWeaponMaterial;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public class DgWeaponItemUtil {
    private static final String MELEE_DAMAGE = "dungeons:melee_damage";
    private static final String SWING_SPEED = "dungeons:swing_speed";
    private static final String RANGED_DAMAGE = "dungeons:ranged_damage";
    private static final String DRAW_TIME = "dungeons:draw_time";

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
        DgItemQuality quality = DgItemUtil.qualityOf(stack);
        Text qualityText = quality == null ? new LiteralText("!! Null Quality !!").formatted(Formatting.RED) : new TranslatableText(quality.getTranslationKey()).styled(style -> style.withItalic(false).withColor(TextColor.fromRgb(quality.getTier().getColor())));
        DgItemUtil.addLore(stack, new TranslatableText(DgTranslationUtil.translationKeyOf("quality", "prefix")).styled(style -> style.withColor(Formatting.GRAY).withItalic(false)).append(qualityText));

        if (item instanceof DgFlavorTextProvider) {
            ((DgFlavorTextProvider) item).applyFlavorText(stack);
        }

        if (item instanceof DgMeleeWeapon) {
            ((DgMeleeWeapon) item).applyMeleeWeaponAttributes(stack);
        }

        if (item instanceof DgRangedWeapon) {
            ((DgRangedWeapon) item).applyRangedWeaponAttributes(stack);
        }

        if (item instanceof DgAttributeProvider) {
            ((DgAttributeProvider) item).applyAttributesText(stack);
        }
    }

    public static ItemStack initWeapon(ItemStack stack, DgItemQuality quality) {
        stack.getOrCreateTag().putString(DgItemUtil.QUALITY, quality.getId());
        DgItemUtil.addCustomModel(stack, DgItemUtil.idPathOf(stack.getItem()));
        finishWeaponInit(stack);
        return stack;
    }

    public static <M extends Enum<M> & DgMaterial> ItemStack initMaterialWeapon(ItemStack stack, M material, DgItemQuality quality) {
        stack.getOrCreateTag().putString(DgItemUtil.QUALITY, quality.getId());
        stack.getOrCreateTag().putString(DgItemUtil.MATERIAL, material.getId());
        DgItemUtil.addCustomModel(stack, material.getId(), DgItemUtil.idPathOf(stack.getItem()));
        finishWeaponInit(stack);
        return stack;
    }

    public static <M extends Enum<M> & DgMeleeWeaponMaterial> ItemStack initMeleeMaterialWeapon(ItemStack stack, M material, DgItemQuality quality, double baseMeleeDamage, double baseSwingSpeed) {
        stack.getOrCreateTag().putDouble(MELEE_DAMAGE, (baseMeleeDamage + (DgItemUtil.RANDOM.nextDouble() / 2)) * material.getMeleeDamageMultiplier() * quality.getDamageMultiplier());
        stack.getOrCreateTag().putDouble(SWING_SPEED, baseSwingSpeed);
        return initMaterialWeapon(stack, material, quality);
    }

    public static <M extends Enum<M> & DgRangedWeaponMaterial> ItemStack initRangedMaterialWeapon(ItemStack stack, M material, DgItemQuality quality, double baseRangedDamage, int baseDrawTime) {
        stack.getOrCreateTag().putDouble(RANGED_DAMAGE, (baseRangedDamage + (DgItemUtil.RANDOM.nextDouble() / 2)) * material.getRangedDamageMultiplier() * quality.getDamageMultiplier());
        stack.getOrCreateTag().putInt(DRAW_TIME, baseDrawTime);
        return initMaterialWeapon(stack, material, quality);
    }
}
