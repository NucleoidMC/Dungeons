package xyz.nucleoid.dungeons.dungeons.util.item;

public class DgFlavorText {
    private final String translationKey;
    private final int lines;

    public DgFlavorText(String translationKey, int lines) {
        this.translationKey = translationKey;
        this.lines = lines;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public int getLines() {
        return lines;
    }
}
