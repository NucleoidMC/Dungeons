package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class DgWeaponGenerator {
    public static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    public static final UUID ATTACK_SPEED_MODIFIER_ID = UUID.fromString("EBE5A195-79B6-4BDC-9B6C-1011FADF041D");

    public static ItemStack generate(Random random) {
        int rand = (int) Math.round(random.nextFloat() * 10.0);

        if (rand <= 2) {
            return DgBow.generate(random).toItemStack();
        } else if (rand <= 8) {
            return DgMetalMeleeWeapon.generate(random).toItemStack();
        } else {
            return DgQuarterstaff.generate(random).toItemStack();
        }
    }

    public static void addWeaponInfoWrapped(ItemStackBuilder builder, String text) {
        DgWeaponGenerator.addTextWrapped(builder, text, Style.EMPTY.withItalic(false).withFormatting(Formatting.GRAY));
    }

    public static void addLoreWrapped(ItemStackBuilder builder, String text) {
        DgWeaponGenerator.addTextWrapped(builder, text, Style.EMPTY.withItalic(false).withFormatting(Formatting.DARK_GRAY));
    }

    public static void addTextWrapped(ItemStackBuilder builder, String text, Style style) {
        int maxLoreLength = 25;
        String[] split = text.split(" ");
        StringBuilder currentLine = new StringBuilder(split[0]);

        for (int idx = 1; idx < split.length; idx++) { // skip first word
            String word = split[idx];
            if (currentLine.length() + word.length() <= maxLoreLength) {
                currentLine.append(" ");
            } else {
                builder.addLore(new LiteralText(currentLine.toString()).setStyle(style));
                currentLine.setLength(0);
            }
            currentLine.append(word);
        }

        builder.addLore(new LiteralText(currentLine.toString()).setStyle(style));
    }

    public static ItemStack fakeWeaponStats(ItemStackBuilder builder, double attackDamage, double attackSpeed) {
        Style statStyle = Style.EMPTY.withItalic(false).withFormatting(Formatting.DARK_GREEN);
        Style headerStyle = Style.EMPTY.withItalic(false).withFormatting(Formatting.GRAY);

        ItemStack stack = builder
                .addLore(new LiteralText(""))
                .addLore(new LiteralText("When in Main Hand:").setStyle(headerStyle))
                .addLore(new LiteralText(String.format(Locale.ENGLISH, " %.2f Attack Damage", attackDamage)).setStyle(statStyle))
                .addLore(new LiteralText(String.format(Locale.ENGLISH, " %.2f Attack Speed", attackSpeed)).setStyle(statStyle))
                .build();

        // https://minecraft.gamepedia.com/Tutorials/Command_NBT_tags#Items
        //  dmg/speed   unbreakable
        byte flags = 2 + 4;
        stack.getOrCreateTag().putByte("HideFlags", flags);

        return stack;
    }
}
