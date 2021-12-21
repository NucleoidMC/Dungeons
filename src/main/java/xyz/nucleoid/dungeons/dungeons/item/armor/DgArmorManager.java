package xyz.nucleoid.dungeons.dungeons.item.armor;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.Dungeons;
import xyz.nucleoid.dungeons.dungeons.item.items.DgItems;
import xyz.nucleoid.dungeons.dungeons.item.material.DgArmorMaterial;

import java.util.HashMap;

public class DgArmorManager {
    private final double[] equipmentBase = new double[]{1, 2, 3, 2};
    private final HashMap<Identifier, DgArmorItem> items = new HashMap<>();

    public DgArmorManager register() {
        for (DgArmorMaterial material : DgArmorMaterial.values()) {
            this.add(material.id + "_boots", new DgArmorItem(material, EquipmentSlot.FEET, this.equipmentBase[0], new FabricItemSettings()));
            this.add(material.id + "_leggings", new DgArmorItem(material, EquipmentSlot.LEGS, this.equipmentBase[1], new FabricItemSettings()));
            this.add(material.id + "_chestplate", new DgArmorItem(material, EquipmentSlot.CHEST, this.equipmentBase[2], new FabricItemSettings()));
            this.add(material.id + "_helmet", new DgArmorItem(material, EquipmentSlot.HEAD, this.equipmentBase[3], new FabricItemSettings()));
        }
        return this;
    }

    private void add(String id, DgArmorItem item) {
        DgItems.add(id, item);
        this.items.put(new Identifier(Dungeons.ID, id ), item);
    }

    @Nullable
    public DgArmorItem getArmor(EquipmentSlot slot, DgArmorMaterial material) {
        return this.items.getOrDefault(new Identifier(Dungeons.ID, getId(slot, material)), null);
    }

    private static String getId(EquipmentSlot slot, DgArmorMaterial material) {
        return switch (slot) {
            case FEET -> material.id + "_boots";
            case LEGS -> material.id + "_leggings";
            case CHEST -> material.id + "_chestplate";
            case HEAD -> material.id + "_helmet";
            default -> "";
        };
    }
}
