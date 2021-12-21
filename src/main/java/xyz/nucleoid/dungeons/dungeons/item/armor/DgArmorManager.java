package xyz.nucleoid.dungeons.dungeons.item.armor;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.Dungeons;
import xyz.nucleoid.dungeons.dungeons.item.items.DgItems;
import xyz.nucleoid.dungeons.dungeons.item.material.DgArmorMaterial;

import java.util.Arrays;
import java.util.HashMap;

public class DgArmorManager {
    private final HashMap<Identifier, DgArmorItem> items = new HashMap<>();

    public DgArmorManager register() {
        for (DgArmorMaterial material : DgArmorMaterial.values()) {
            this.add(EquipmentSlot.FEET, material);
            this.add(EquipmentSlot.LEGS, material);
            this.add(EquipmentSlot.CHEST, material);
            this.add(EquipmentSlot.HEAD, material);
        }

        return this;
    }

    private void add(EquipmentSlot slot, DgArmorMaterial material) {
        DgArmorItem item = new DgArmorItem(material, slot, new FabricItemSettings());
        String id = getId(slot, material);
        DgItems.add(id, item);
        this.items.put(new Identifier(Dungeons.ID, id), item);
    }

    @Nullable
    public DgArmorItem getArmor(EquipmentSlot slot, DgArmorMaterial material) {
        return this.items.get(new Identifier(Dungeons.ID, getId(slot, material)));
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
