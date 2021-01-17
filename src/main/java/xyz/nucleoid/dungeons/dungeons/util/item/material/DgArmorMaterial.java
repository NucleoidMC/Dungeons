package xyz.nucleoid.dungeons.dungeons.util.item.material;

import xyz.nucleoid.dungeons.dungeons.util.item.DgItemQuality;

public enum DgArmorMaterial implements DgMaterial {

    // Tier 1
    LEATHER("leather"),
    ;

    public String id;

    DgArmorMaterial(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public DgItemQuality getMinQuality() {
        return DgItemQuality.BATTERED;
    }

    @Override
    public DgItemQuality getMaxQuality() {
        return DgItemQuality.MYTHICAL;
    }
}
