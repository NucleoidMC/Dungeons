package xyz.nucleoid.dungeons.dungeons.util.item.material;

import xyz.nucleoid.dungeons.dungeons.util.DgTranslationUtil;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemQuality;

public interface DgMaterial {
    String getId();

    DgItemQuality getMinQuality();

    DgItemQuality getMaxQuality();

    default String getTranslationKey() {
        return DgTranslationUtil.translationKeyOf("material", getId());
    }
}
