package xyz.nucleoid.dungeons.dungeons.util.item;

import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgArmorMaterial;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMaterial;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public class DgArmorUtil {
    public static ItemStackBuilder armorBuilder(Item item) {
        return ItemStackBuilder.of(item);
    }

    public static void finishArmorInit(ItemStack stack) {
        Item item = stack.getItem();
        stack.setCustomName(item.getName(stack));
    }
    public static ItemStack initArmor(ItemStack stack, DgArmorMaterial material, DgItemQuality quality) {
        stack.getOrCreateTag().putString(DgItemUtil.QUALITY, quality.getId());
        stack.getOrCreateTag().putString(DgItemUtil.MATERIAL, material.getId());
        if (material.placeholder == ArmorMaterials.LEATHER) {
            stack.getOrCreateSubTag("display").putInt("color", material.color);
        }
        finishArmorInit(stack);
        return stack;
    }

}
