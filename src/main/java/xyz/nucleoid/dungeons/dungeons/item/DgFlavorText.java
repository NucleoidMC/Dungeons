package xyz.nucleoid.dungeons.dungeons.item;

public record DgFlavorText(String translationKey, int lines) {

    public String getTranslationKey() {
        return this.translationKey;
    }

    public int getLines() {
        return this.lines;
    }
}
