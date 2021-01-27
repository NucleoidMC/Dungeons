package xyz.nucleoid.dungeons.dungeons.util.item.loot;

import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.item.base.DgMaterialItem;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMaterial;

import java.util.Random;

public class DgArmorLoot {

    public static ItemStack generate(Random random, double dungeonLevel) {
        double meanLevel = dungeonLevel - 0.5;

        getMaterialComponent().getMaterialPicker().randomlyPick(random, dungeonLevel);
        return item.createStack(material, DgItemQuality.chooseInRange(random, material.getMinQuality(), material.getMaxQuality()));
    }


}
