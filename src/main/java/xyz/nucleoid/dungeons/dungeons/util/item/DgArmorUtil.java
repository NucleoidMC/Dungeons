package xyz.nucleoid.dungeons.dungeons.util.item;

import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xyz.nucleoid.dungeons.dungeons.item.armor.DgArmorItem;
import xyz.nucleoid.dungeons.dungeons.item.base.DgFlavorTextProvider;
import xyz.nucleoid.dungeons.dungeons.util.DgTranslationUtil;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgArmorMaterial;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public class DgArmorUtil {

    /**
     * This is the scale that is used to calculate the toughness based on the piece (Boots / Leggings / Chestplate / Helmet)s
     */


    public static ItemStackBuilder armorBuilder(Item item) {
        return ItemStackBuilder.of(item);
    }

    public static void finishArmorInit(ItemStack stack) {
        Item item = stack.getItem();
        stack.setCustomName(item.getName(stack));

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

        if (item instanceof DgArmorItem) {
            ((DgArmorItem) item).applyArmorAttributes(stack);
        }
    }
    public static ItemStack initArmor(ItemStack stack, DgItemQuality quality) {
        DgArmorMaterial material = ((DgArmorItem) stack.getItem()).getMaterial();
        stack.getOrCreateTag().putString(DgItemUtil.QUALITY, quality.getId());
        stack.getOrCreateTag().putString(DgItemUtil.MATERIAL, material.getId());
        if (material.placeholder == ArmorMaterials.LEATHER) {
            stack.getOrCreateSubTag("display").putInt("color", material.color);
        }
        finishArmorInit(stack);
        return stack;
    }

    public static double calculateArmorToughness(double base, double roll, double materialMultiplier, double qualityMultiplier) {
        return (base + (roll / 4)) * materialMultiplier * qualityMultiplier;
    }

    public static double getKnockback(ItemStack stack, double base) {
        if (!(stack.getItem() instanceof DgArmorItem)) {
            System.err.println("Tried to calculate armor toughness for an incompatible item.");
            return 0;
        }
        DgArmorMaterial material = ((DgArmorItem) stack.getItem()).getMaterial();
        return base * material.getToughnessMultiplier();
    }

    public static double getArmorToughness(ItemStack stack) {
        if (!(stack.getItem() instanceof DgArmorItem)) {
            System.err.println("Tried to calculate armor toughness for an incompatible item.");
            return 0;
        }
        DgArmorMaterial material = ((DgArmorItem) stack.getItem()).getMaterial();
        DgItemQuality quality = DgItemUtil.qualityOf(stack);
        DgArmorItem item = (DgArmorItem) stack.getItem();
        double roll = DgItemUtil.rollOf(stack);
        return DgArmorUtil.calculateArmorToughness(item.getBaseToughness(), roll, material.getToughnessMultiplier(), quality.getMultiplier());
    }
}
