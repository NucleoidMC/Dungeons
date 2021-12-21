package xyz.nucleoid.dungeons.dungeons.item.material;

import xyz.nucleoid.dungeons.dungeons.util.DgTranslationUtil;
import xyz.nucleoid.dungeons.dungeons.item.DgItemQuality;

public interface DgMaterial {
    String getId();

    DgItemQuality getMinQuality();

    DgItemQuality getMaxQuality();

    default String getTranslationKey() {
        return DgTranslationUtil.translationKeyOf("material", this.getId());
    }
}
