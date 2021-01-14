package xyz.nucleoid.dungeons.dungeons.util;

import xyz.nucleoid.dungeons.dungeons.Dungeons;

public class DgTranslationUtil {
    public static String translationKeyOf(String type, String id) {
        return type + "." + Dungeons.ID + "." + id;
    }
}
