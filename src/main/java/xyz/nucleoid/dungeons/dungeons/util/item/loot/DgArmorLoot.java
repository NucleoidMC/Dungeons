package xyz.nucleoid.dungeons.dungeons.util.item.loot;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.item.DgItems;
import xyz.nucleoid.dungeons.dungeons.item.armor.DgArmorItem;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgArmorMaterial;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgRangedWeaponWood;

import java.util.Random;

public class DgArmorLoot {
    public static ItemStack generate(Random random, double dungeonLevel) {
        DgArmorMaterial material = DgArmorMaterial.choose(random, dungeonLevel);
        DgItemQuality quality = DgItemQuality.chooseInRange(random, material.minQuality, material.maxQuality);
        // Choose a random piece of armor
        EquipmentSlot slot = EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, random.nextInt(4));
        DgArmorItem item = DgItems.ARMOR_MANAGER.getArmor(slot, material);
        return item != null ? item.createStack(quality) : ItemStack.EMPTY;
    }
}
