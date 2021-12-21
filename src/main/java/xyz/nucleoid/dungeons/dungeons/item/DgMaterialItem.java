package xyz.nucleoid.dungeons.dungeons.item;

import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.item.material.DgMaterial;
import xyz.nucleoid.dungeons.dungeons.item.material.DgMaterialComponent;

public interface DgMaterialItem<M extends Enum<M> & DgMaterial> {
    DgMaterialComponent<M> getMaterialComponent();

    ItemStack createStack(M material, DgItemQuality quality);

    default ItemStack createStackInternal(Enum<?> material, DgItemQuality quality) {
        return this.createStack((M) material, quality);
    }
}
