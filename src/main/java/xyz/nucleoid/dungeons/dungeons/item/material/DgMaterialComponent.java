package xyz.nucleoid.dungeons.dungeons.item.material;

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
        for (M material : this.getMaterials()) {
            this.idMap.put(material.getId(), material);
        }
    }

    public Stream<M> streamMaterials() {
        return Arrays.stream(this.getMaterials());
    }

    public M[] getMaterials() {
        return this.materialEnum.getEnumConstants();
    }

    public M getMaterial(String id) {
        return this.idMap.getOrDefault(id, null);
    }

    public DgMaterialPicker<M> getMaterialPicker() {
        return this.materialPicker;
    }
}
