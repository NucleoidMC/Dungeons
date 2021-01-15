package xyz.nucleoid.dungeons.dungeons.item.base;

import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.util.item.DgFlavorText;
import xyz.nucleoid.dungeons.dungeons.util.DgTranslationUtil;

import java.util.Random;

public interface DgFlavorTextProvider {
    DgFlavorText getFlavorText(Random random, ItemStack stack);

    String getFlavorTextType();

    default DgFlavorText flavorTextOf(String id, int lines) {
        return new DgFlavorText(DgTranslationUtil.translationKeyOf("flavor_text", getFlavorTextType() + "." + id), lines);
    }
}
