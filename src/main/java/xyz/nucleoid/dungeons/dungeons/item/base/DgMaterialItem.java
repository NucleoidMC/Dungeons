package xyz.nucleoid.dungeons.dungeons.item.base;

import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMaterial;
import xyz.nucleoid.dungeons.dungeons.util.item.DgMaterialComponent;

public interface DgMaterialItem<M extends Enum<M> & DgMaterial> {
    DgMaterialComponent<M> getMaterialComponent();

    ItemStack createStack(M material, DgItemQuality quality);

    default ItemStack createStackInternal(Enum<?> material, DgItemQuality quality) {
        return createStack((M) material, quality);
    }
}
