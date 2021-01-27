package xyz.nucleoid.dungeons.dungeons.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.dungeons.dungeons.Dungeons;
import xyz.nucleoid.dungeons.dungeons.item.armor.DgArmorItem;
import xyz.nucleoid.dungeons.dungeons.item.base.DgModelProvider;
import xyz.nucleoid.dungeons.dungeons.item.melee.DgMaterialMeleeWeaponItem;
import xyz.nucleoid.dungeons.dungeons.item.melee.DgMetalMeleeWeaponItem;
import xyz.nucleoid.dungeons.dungeons.item.melee.DgMorningStarItem;
import xyz.nucleoid.dungeons.dungeons.item.ranged.DgMaterialBowItem;
import xyz.nucleoid.dungeons.dungeons.item.ranged.DgMaterialCrossbowItem;
import xyz.nucleoid.dungeons.dungeons.item.ranged.DgWoodBowItem;
import xyz.nucleoid.dungeons.dungeons.item.ranged.DgWoodCrossbowItem;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgQuarterstaffWood;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgRangedWeaponWood;

import java.util.*;

public class DgItems {
    private static final Map<Identifier, Item> REGISTRY = new HashMap<>();

    public static final DgMetalMeleeWeaponItem HAND_AXE = add("hand_axe", new DgMetalMeleeWeaponItem(6, 1.1, Items.IRON_AXE, new FabricItemSettings()));
    public static final DgMetalMeleeWeaponItem BATTLE_AXE = add("battle_axe", new DgMetalMeleeWeaponItem(7, 0.8, Items.DIAMOND_AXE, new FabricItemSettings()));
    public static final DgMetalMeleeWeaponItem BROADSWORD = add("broadsword", new DgMetalMeleeWeaponItem(5, 1.6, Items.DIAMOND_SWORD, new FabricItemSettings()));
    public static final DgMetalMeleeWeaponItem GREAT_SWORD = add("great_sword", new DgMetalMeleeWeaponItem(7, 0.8, Items.IRON_SWORD, new FabricItemSettings()));
    public static final DgMetalMeleeWeaponItem DAGGER = add("dagger", new DgMetalMeleeWeaponItem(2.5, 3.2, Items.STICK, new FabricItemSettings()));
    public static final DgMetalMeleeWeaponItem MORNING_STAR = add("morning_star", new DgMorningStarItem(4.5, 1.5, Items.STICK, new FabricItemSettings()));
    public static final DgMaterialMeleeWeaponItem<DgQuarterstaffWood> QUARTERSTAFF = add("quarterstaff", new DgMaterialMeleeWeaponItem<>(4.7, 1.8, new DgMaterialComponent<>(DgQuarterstaffWood.class, DgQuarterstaffWood::choose), Items.STICK, new FabricItemSettings()));

    public static final DgMaterialBowItem<DgRangedWeaponWood> SHORTBOW = add("shortbow", new DgWoodBowItem(4.6, 15, new FabricItemSettings()));
    public static final DgMaterialBowItem<DgRangedWeaponWood> LONGBOW = add("longbow", new DgWoodBowItem(6.2, 20, new FabricItemSettings()));
    public static final DgMaterialCrossbowItem<DgRangedWeaponWood> CROSSBOW = add("crossbow", new DgWoodCrossbowItem(7, 25, new FabricItemSettings()));

    public static final DgArmorItem ARMOR_HEAD = add("armor_head")

    public static final DgShard SHARD = add("shard", new DgShard(new FabricItemSettings()));

    private static <I extends Item> I add(String id, I item) {
        REGISTRY.put(new Identifier(Dungeons.ID, id), item);
        return item;
    }

    public static void register() {
        REGISTRY.forEach((id, item) -> {
            Registry.register(Registry.ITEM, id, item);
            if (item instanceof DgModelProvider) {
                ((DgModelProvider) item).registerModels();
            }
        });
    }

    public static Collection<Item> getItems() {
        return REGISTRY.values();
    }
}
