package xyz.nucleoid.dungeons.dungeons.util.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
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
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgWeaponMaterial;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public class DgWeaponItemUtil {
    public static ItemStackBuilder weaponBuilder(Item item) {
        return ItemStackBuilder.of(item);
    }

    private static void finishWeaponInit(ItemStack stack) {
        Item item = stack.getItem();
        stack.setCustomName(item.getName(stack));
        // https://minecraft.gamepedia.com/Tutorials/Command_NBT_tags#Items
        // hide all, any tooltip info will be added ourselves
        byte flags = 1 + 2 + 4 + 8 + 16 + 32 + 64;
        CompoundTag nbt = stack.getOrCreateTag();
        nbt.putByte("HideFlags", flags);
        if (!nbt.contains(DgItemUtil.ROLL)) {
            nbt.putDouble(DgItemUtil.ROLL, DgItemUtil.RANDOM.nextDouble());
        }
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

    public static <M extends Enum<M> & DgWeaponMaterial> ItemStack initMeleeMaterialWeapon(ItemStack stack, M material, DgItemQuality quality, double baseMeleeDamage, double baseSwingSpeed) {
        return initMaterialWeapon(stack, material, quality);
    }

    public static double calculateDamage(double base, double roll, double materialMultiplier, double qualityMultiplier) {
        return (base + (roll / 2)) * materialMultiplier * qualityMultiplier;
    }

    public static <M extends Enum<M> & DgWeaponMaterial> double getDamage(ItemStack stack, double base, DgMaterialComponent<M> materialComponent) {
        M material = DgItemUtil.materialOf(stack, materialComponent);
        DgItemQuality quality = DgItemUtil.qualityOf(stack);
        double roll = DgItemUtil.rollOf(stack);
        return DgWeaponItemUtil.calculateDamage(base, roll, material.getDamageMultiplier(), quality.getMultiplier());
    }
}
