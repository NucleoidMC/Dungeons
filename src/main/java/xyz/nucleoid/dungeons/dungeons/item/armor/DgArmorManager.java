package xyz.nucleoid.dungeons.dungeons.item.armor;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import xyz.nucleoid.dungeons.dungeons.item.DgItems;
import xyz.nucleoid.dungeons.dungeons.item.base.DgArmor;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgArmorMaterial;

import java.util.HashMap;

public class DgArmorManager {
    private final double[] equipmentBase = new double[]{1, 2, 3, 2};
    private final HashMap<String, Item> items = new HashMap<>();

    public DgArmorManager register() {
        for (DgArmorMaterial material : DgArmorMaterial.values()) {
            Item boots = DgItems.add(material.id + "_boots", new DgArmorItem(material, EquipmentSlot.FEET, equipmentBase[0], new FabricItemSettings()));
            items.put(material.id + "_boots", boots);
            Item leggings = DgItems.add(material.id + "_leggings", new DgArmorItem(material, EquipmentSlot.LEGS, equipmentBase[1], new FabricItemSettings()));
            items.put(material.id + "_leggings", leggings);
            Item chestplate = DgItems.add(material.id + "_chestplate", new DgArmorItem(material, EquipmentSlot.CHEST, equipmentBase[2], new FabricItemSettings()));
            items.put(material.id + "_chestplate", chestplate);
            Item helmet = DgItems.add(material.id + "_helmet", new DgArmorItem(material, EquipmentSlot.HEAD, equipmentBase[3], new FabricItemSettings()));
            items.put(material.id + "_helmet", helmet);
        }
        return this;
    }

    public Item getArmor(EquipmentSlot slot, DgArmorMaterial material) {
        return items.getOrDefault(getId(slot, material), Items.AIR);
    }

    private String getId(EquipmentSlot slot, DgArmorMaterial material) {
        switch (slot) {
            case FEET:
                return material.id + "_boots";
            case LEGS:
                return material.id + "_leggings";
            case CHEST:
                return material.id + "_chestplate";
            case HEAD:
                return material.id + "_helmet";
            default:
                return "";
        }
    }


}
