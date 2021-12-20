package xyz.nucleoid.dungeons.dungeons.item.armor;

import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.item.DgItemUtil;
import xyz.nucleoid.dungeons.dungeons.item.material.DgArmorMaterial;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public class DgArmorUtil {

    /**
     * This is the scale that is used to calculate the toughness based on the piece (Boots / Leggings / Chestplate / Helmet)s
     */


    public static ItemStackBuilder armorBuilder(Item item) {
        return ItemStackBuilder.of(item);
    }

    public static void finishArmorInit(ItemStack stack) {
        Item item = DgItemUtil.finishArmorOrWeapon(stack);

        if (item instanceof DgArmorItem) {
            ((DgArmorItem) item).applyArmorAttributes(stack);
        }
    }

    public static ItemStack initArmor(ItemStack stack, DgItemQuality quality) {
        DgArmorMaterial material = ((DgArmorItem) stack.getItem()).getMaterial();
        stack.getOrCreateNbt().putString(DgItemUtil.QUALITY, quality.getId());
        stack.getOrCreateNbt().putString(DgItemUtil.MATERIAL, material.getId());
        if (material.placeholder == ArmorMaterials.LEATHER) {
            stack.getOrCreateSubNbt("display").putInt("color", material.color);
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
        if (!(stack.getItem() instanceof DgArmorItem item)) {
            System.err.println("Tried to calculate armor toughness for an incompatible item.");
            return 0;
        }
        DgArmorMaterial material = ((DgArmorItem) stack.getItem()).getMaterial();
        DgItemQuality quality = DgItemUtil.qualityOf(stack);
        double roll = DgItemUtil.rollOf(stack);
        return DgArmorUtil.calculateArmorToughness(item.getBaseToughness(), roll, material.getToughnessMultiplier(), quality.getMultiplier());
    }
}
