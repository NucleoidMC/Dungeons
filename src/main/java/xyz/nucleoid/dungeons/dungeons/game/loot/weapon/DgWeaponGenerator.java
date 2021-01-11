package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

import java.util.Random;
import java.util.UUID;

public class DgWeaponGenerator {
    public static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

    public static ItemStack generate(Random random) {
        int rand = (int) Math.round(random.nextFloat() * 10.0);

        if (rand <= 3) {
            return DgBow.generate(random).toItemStack();
        } else if (rand <= 7) {
            return DgMetalMeleeWeapon.generate(random).toItemStack();
        } else {
            return DgQuarterstaff.generate(random).toItemStack();
        }
    }

    public static void addLoreWrapped(ItemStackBuilder builder, String text) {
        int maxLoreLength = 25;
        String[] split = text.split(" ");
        StringBuilder currentLine = new StringBuilder(split[0]);

        for (int idx = 1; idx < split.length; idx++) { // skip first word
            String word = split[idx];
            if (currentLine.length() + word.length() <= maxLoreLength) {
                currentLine.append(" ");
            } else {
                builder.addLore(new LiteralText(currentLine.toString()));
                currentLine.setLength(0);
            }
            currentLine.append(word);
        }

        builder.addLore(new LiteralText(currentLine.toString()));
    }
}
