package xyz.nucleoid.dungeons.dungeons.loot;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.item.armor.DgArmorItem;
import xyz.nucleoid.dungeons.dungeons.item.items.DgItems;
import xyz.nucleoid.dungeons.dungeons.item.material.DgArmorMaterial;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DgArmorLoot {
    public static ItemStack generate(Random random, double dungeonLevel) {
        DgArmorMaterial material = DgArmorMaterial.choose(random, dungeonLevel);
        DgItemQuality quality = DgItemQuality.chooseInRange(random, material.minQuality, material.maxQuality);

        // Choose a random piece of armor
        EquipmentSlot slot = EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, random.nextInt(4));
        DgArmorItem item = DgItems.ARMOR_MANAGER.getArmor(slot, material);
        assert item != null;

        return item.createStack(quality);
    }

    public static ItemStack generateFixed(Random random, DgItemQuality quality) {
        List<DgArmorMaterial> materialList = Arrays.stream(DgArmorMaterial.values()).filter(mat -> mat.canBeOfQuality(quality)).toList();
        DgArmorMaterial material = materialList.get(random.nextInt(materialList.size()));

        // Choose a random piece of armor
        EquipmentSlot slot = EquipmentSlot.fromTypeIndex(EquipmentSlot.Type.ARMOR, random.nextInt(4));
        DgArmorItem item = DgItems.ARMOR_MANAGER.getArmor(slot, material);
        assert item != null;

        return item.createStack(quality);
    }
}
