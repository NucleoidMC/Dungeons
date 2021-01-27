package xyz.nucleoid.dungeons.dungeons.util.item.material;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import xyz.nucleoid.dungeons.dungeons.item.armor.DgArmorType;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemQuality;

import java.util.Random;

public enum DgArmorMaterial implements DgMaterial, ArmorMaterial {
    // Light Armor
    CLOTH("cloth", 1F, 0F, DgArmorType.LIGHT_ARMOR, DgItemQuality.BATTERED, DgItemQuality.FINE, ArmorMaterials.LEATHER, 14609387),  //#deebeb
    IRON_THREAD("iron_thread", 2F, 0F, DgArmorType.LIGHT_ARMOR, DgItemQuality.MEDIOCRE, DgItemQuality.SUPERB, ArmorMaterials.LEATHER, 13751765), //#d1d5d5
    SILK("silk", 1.5F, 0F, DgArmorType.LIGHT_ARMOR, DgItemQuality.MEDIOCRE, DgItemQuality.LEGENDARY, ArmorMaterials.LEATHER, 16448205), //#fafacd
    // Medium Armor
    LEATHER("leather", 4F, 0F, DgArmorType.MEDIUM_ARMOR, DgItemQuality.BATTERED, DgItemQuality.FINE, ArmorMaterials.LEATHER, 10511680), //Default
    CHAINS("chains", 6F, 0F, DgArmorType.MEDIUM_ARMOR, DgItemQuality.MEDIOCRE, DgItemQuality.SUPERB, ArmorMaterials.CHAIN, -1),
    HIDE("hide", 3F, 0F, DgArmorType.MEDIUM_ARMOR, DgItemQuality.MEDIOCRE, DgItemQuality.LEGENDARY, ArmorMaterials.LEATHER, 13467442), // #cd7f32
    // Heavy Armor
    IRON_PLATING("iron_plate", 10F, 2F, DgArmorType.HEAVY_ARMOR, DgItemQuality.BATTERED, DgItemQuality.FINE, ArmorMaterials.IRON, -1),
    DIAMOND_PLATING("diamond_plate", 12F, 2F, DgArmorType.HEAVY_ARMOR, DgItemQuality.MEDIOCRE, DgItemQuality.SUPERB, ArmorMaterials.DIAMOND, -1),
    NETHERITE_PLATING("netherite_plate", 15F, 3F, DgArmorType.HEAVY_ARMOR, DgItemQuality.MEDIOCRE, DgItemQuality.LEGENDARY, ArmorMaterials.NETHERITE, -1),
    ;
    /**
     * This is the scale that is used to calculate the toughness based on the piece (Boots / Leggings / Chestplate / Helmet)s
     */
    private static final int[] partToughness = new int[]{1,2,3,2};

    public String id;
    public float baseToughness;
    public float baseKnockbackResistance;
    public DgArmorType type;
    public DgItemQuality minQuality;
    public DgItemQuality maxQuality;
    public ArmorMaterials placeholder;
    public int color;

    DgArmorMaterial(String id, float baseToughness, float baseKnockbackResistance, DgArmorType type, DgItemQuality minQuality, DgItemQuality maxQuality, ArmorMaterials placeholder, int color) {
        this.id = id;
        this.baseToughness = baseToughness;
        this.baseKnockbackResistance = baseKnockbackResistance;
        this.type = type;
        this.minQuality = minQuality;
        this.maxQuality = maxQuality;
        this.placeholder = placeholder;
        this.color = color;
    }

    public static DgArmorMaterial choose(Random random, double meanLevel) {
        double number = random.nextGaussian() * 0.8 + meanLevel;
        int ord = (int) Math.round(number);

        DgArmorMaterial[] types = DgArmorMaterial.values();
        return types[Math.max(0, Math.min(types.length, ord))];
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public DgItemQuality getMinQuality() {
        return minQuality;
    }

    @Override
    public DgItemQuality getMaxQuality() {
        return maxQuality;
    }

    public float getToughness(EquipmentSlot slot) {
        return partToughness[slot.getEntitySlotId()] * baseToughness;
    }

    @Override
    public int getDurability(EquipmentSlot slot) {
        return placeholder.getDurability(slot);
    }

    @Override
    public int getProtectionAmount(EquipmentSlot slot) {
        return 0;
    }

    @Override
    public int getEnchantability() {
        return placeholder.getEnchantability();
    }

    @Override
    public SoundEvent getEquipSound() {
        return placeholder.getEquipSound();
    }

    @Override
    public Ingredient getRepairIngredient() {
        return null;
    }

    @Override
    public String getName() {
        return id;
    }
    /**
     * @deprecated This function is only here because of the ArmorMaterial constraint. You should instead use getToughness(EquipmentSlot slot)
     * @return The default toughness fixed by the placeholder item.
     */
    @Deprecated
    @Override
    public float getToughness() {
        return placeholder.getToughness();
    }

    public float getKnockbackResistance() {
        return baseKnockbackResistance;
    }

    public Item getPlaceholderItem(EquipmentSlot slot) {
        switch (placeholder) {
            case LEATHER:
                switch (slot) {
                    case FEET:
                        return Items.LEATHER_BOOTS;
                    case LEGS:
                        return Items.LEATHER_LEGGINGS;
                    case CHEST:
                        return Items.LEATHER_CHESTPLATE;
                    case HEAD:
                        return Items.LEATHER_HELMET;
                }
                break;
            case CHAIN:
                switch (slot) {
                    case FEET:
                        return Items.CHAINMAIL_BOOTS;
                    case LEGS:
                        return Items.CHAINMAIL_LEGGINGS;
                    case CHEST:
                        return Items.CHAINMAIL_CHESTPLATE;
                    case HEAD:
                        return Items.CHAINMAIL_HELMET;
                }
                break;
            case IRON:
                switch (slot) {
                    case FEET:
                        return Items.IRON_BOOTS;
                    case LEGS:
                        return Items.IRON_LEGGINGS;
                    case CHEST:
                        return Items.IRON_CHESTPLATE;
                    case HEAD:
                        return Items.IRON_HELMET;
                }
                break;
            case GOLD:
                switch (slot) {
                    case FEET:
                        return Items.GOLDEN_BOOTS;
                    case LEGS:
                        return Items.GOLDEN_LEGGINGS;
                    case CHEST:
                        return Items.GOLDEN_CHESTPLATE;
                    case HEAD:
                        return Items.GOLDEN_HELMET;
                }
                break;
            case DIAMOND:
                switch (slot) {
                    case FEET:
                        return Items.DIAMOND_BOOTS;
                    case LEGS:
                        return Items.DIAMOND_LEGGINGS;
                    case CHEST:
                        return Items.DIAMOND_CHESTPLATE;
                    case HEAD:
                        return Items.DIAMOND_HELMET;
                }
                break;
            case NETHERITE:
                switch (slot) {
                    case FEET:
                        return Items.NETHERITE_BOOTS;
                    case LEGS:
                        return Items.NETHERITE_LEGGINGS;
                    case CHEST:
                        return Items.NETHERITE_CHESTPLATE;
                    case HEAD:
                        return Items.NETHERITE_HELMET;
                }
                break;
        }
        return Items.AIR;
    }
}
