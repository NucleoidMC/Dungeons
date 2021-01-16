package xyz.nucleoid.dungeons.dungeons.item.base;

import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xyz.nucleoid.dungeons.dungeons.util.DgTranslationUtil;
import xyz.nucleoid.dungeons.dungeons.util.item.DgFlavorText;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemUtil;

import java.util.Random;

public interface DgFlavorTextProvider {
    DgFlavorText getFlavorText(Random random, ItemStack stack);

    String getFlavorTextType();

    default DgFlavorText flavorTextOf(String id, int lines) {
        return new DgFlavorText(DgTranslationUtil.translationKeyOf("flavor_text", getFlavorTextType() + "." + id), lines);
    }

    default void applyFlavorText(ItemStack stack) {
        DgFlavorText flavorText = this.getFlavorText(DgItemUtil.RANDOM, stack);
        int lines = flavorText.getLines();
        for (int i = 1; i <= lines; i++) {
            String translationKey = flavorText.getTranslationKey();
            if (lines != 1) {
                translationKey = translationKey + "." + i;
            }
            DgItemUtil.addLore(stack, new TranslatableText(translationKey).styled(style -> style.withItalic(false).withFormatting(Formatting.DARK_GRAY)));
        }
    }
}
