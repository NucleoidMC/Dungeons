package xyz.nucleoid.dungeons.dungeons.util.item;

import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMaterial;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMaterialPicker;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class DgMaterialComponent<M extends Enum<M> & DgMaterial> {
    private final Class<M> materialEnum;
    private final Map<String, M> idMap = new HashMap<>();
    private final DgMaterialPicker<M> materialPicker;

    public DgMaterialComponent(Class<M> materialEnum, DgMaterialPicker<M> materialPicker) {
        this.materialEnum = materialEnum;
        this.materialPicker = materialPicker;
        for (M material : getMaterials()) {
            idMap.put(material.getId(), material);
        }
    }

    public Stream<M> streamMaterials() {
        return Arrays.stream(getMaterials());
    }

    public M[] getMaterials() {
        return materialEnum.getEnumConstants();
    }

    public M getMaterial(String id) {
        return idMap.getOrDefault(id, null);
    }

    public DgMaterialPicker<M> getMaterialPicker() {
        return materialPicker;
    }
}
