package xyz.nucleoid.dungeons.dungeons.fake;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DgItems {
    // TODO(restioson): vanilla sets a max damage?
    public static Item BOW = new DgBowItem(new FabricItemSettings().group(ItemGroup.COMBAT));
    public static Item CROSSBOW = new DgCrossbowItem(new FabricItemSettings().group(ItemGroup.COMBAT));

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier("dungeons", "bow"), BOW);
        Registry.register(Registry.ITEM, new Identifier("dungeons", "crossbow"), CROSSBOW);
    }
}
