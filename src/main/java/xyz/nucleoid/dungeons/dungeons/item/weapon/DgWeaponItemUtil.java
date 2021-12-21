package xyz.nucleoid.dungeons.dungeons.item.weapon;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.item.DgAttributeProvider;
import xyz.nucleoid.dungeons.dungeons.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.item.DgItemUtil;
import xyz.nucleoid.dungeons.dungeons.item.weapon.melee.DgMeleeWeapon;
import xyz.nucleoid.dungeons.dungeons.item.weapon.ranged.DgRangedWeapon;
import xyz.nucleoid.dungeons.dungeons.item.material.DgMaterial;
import xyz.nucleoid.dungeons.dungeons.item.material.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.item.material.DgWeaponMaterial;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public class DgWeaponItemUtil {
    public static ItemStackBuilder weaponBuilder(Item item) {
        return ItemStackBuilder.of(item);
    }

    private static void finishWeaponInit(ItemStack stack) {
        Item item = DgItemUtil.finishArmorOrWeapon(stack);

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

    public static <M extends Enum<M> & DgMaterial> ItemStack initMaterialWeapon(ItemStack stack, M material, DgItemQuality quality) {
        stack.getOrCreateNbt().putString(DgItemUtil.QUALITY, quality.getId());
        stack.getOrCreateNbt().putString(DgItemUtil.MATERIAL, material.getId());
        DgItemUtil.addCustomModel(stack, material.getId(), DgItemUtil.idPathOf(stack.getItem()));
        finishWeaponInit(stack);
        return stack;
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
