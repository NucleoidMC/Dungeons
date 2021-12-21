package xyz.nucleoid.dungeons.dungeons.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import xyz.nucleoid.dungeons.dungeons.Dungeons;
import xyz.nucleoid.dungeons.dungeons.util.DgTranslationUtil;
import xyz.nucleoid.dungeons.dungeons.util.IComparable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public enum DgItemQuality implements IComparable<DgItemQuality> {
    // Tier 1,
    BATTERED("battered", 1.0, DgItemTier.ONE, Items.STICK),
    OLD("old", 1.1, DgItemTier.ONE, Items.ANCIENT_DEBRIS),
    DUSTY("dusty", 1.2, DgItemTier.ONE, Items.SAND),

    // Tier 2
    MEDIOCRE("mediocre", 1.0, DgItemTier.TWO, Items.COBBLESTONE),
    STURDY("sturdy", 1.1, DgItemTier.TWO, Items.IRON_INGOT),
    FINE("fine", 1.2, DgItemTier.TWO, Items.GOLD_INGOT),

    // Tier 3
    EXCELLENT("excellent", 1.1, DgItemTier.THREE, Items.DIAMOND),
    SUPERB("superb", 1.2, DgItemTier.THREE, Items.NETHERITE_INGOT),

    // Tier 4
    LEGENDARY("legendary", 1.0, DgItemTier.FOUR, Items.DRAGON_HEAD),
    MYTHICAL("mythical", 1.1, DgItemTier.FOUR, Items.NETHER_STAR);

    private final String id;
    private final double multiplier;
    private final DgItemTier tier;

    private static final Map<String, DgItemQuality> ID_MAP = new HashMap<>();
    private final ItemGroup itemGroup;

    DgItemQuality(String id, double multiplier, DgItemTier tier, Item icon) {
        this.id = id;
        this.multiplier = multiplier;
        this.tier = tier;
        this.itemGroup = FabricItemGroupBuilder.build(new Identifier(Dungeons.ID, id), icon::getDefaultStack);
    }

    static {
        for (DgItemQuality value : values()) {
            ID_MAP.put(value.getId(), value);
        }
    }

    public static DgItemQuality chooseInRange(Random random, DgItemQuality min, DgItemQuality max) {
        DgItemQuality[] types = DgItemQuality.values();
        int idx = random.nextInt(max.ordinal() - min.ordinal() + 1) + min.ordinal(); // inclusive between min and max
        return types[idx];
    }

    public String getId() {
        return this.id;
    }

    public String getTranslationKey() {
        return DgTranslationUtil.translationKeyOf("quality", this.id);
    }

    public double getMultiplier() {
        return this.multiplier;
    }

    public static DgItemQuality fromId(String id) {
        return ID_MAP.getOrDefault(id, null);
    }

    public ItemGroup getItemGroup() {
        return this.itemGroup;
    }

    public DgItemTier getTier() {
        return this.tier;
    }
}
