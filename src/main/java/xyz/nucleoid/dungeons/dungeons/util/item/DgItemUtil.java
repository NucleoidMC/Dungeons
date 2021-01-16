package xyz.nucleoid.dungeons.dungeons.util.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMaterial;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.util.item.model.DgItemModelRegistry;

import java.util.Random;
import java.util.function.BiFunction;

public class DgItemUtil {
    public static final String QUALITY = "dungeons:quality";
    public static final String MATERIAL = "dungeons:material";
    public static final String ROLL = "dungeons:roll";

    public static final Random RANDOM = new Random();

    public static Identifier idOf(Item item) {
        return Registry.ITEM.getId(item);
    }

    public static String idPathOf(Item item) {
        return idOf(item).getPath();
    }

    public static DgItemQuality qualityOf(ItemStack stack) {
        return DgItemQuality.fromId(stack.getOrCreateTag().getString(QUALITY));
    }

    public static <M extends Enum<M> & DgMaterial> M materialOf(ItemStack stack, DgMaterialComponent<M> materialComponent) {
        return materialComponent.getMaterial(stack.getOrCreateTag().getString(MATERIAL));
    }

    public static double rollOf(ItemStack stack) {
        return stack.getOrCreateTag().getDouble(ROLL);
    }

    public static <M extends Enum<M> & DgMaterial> Text nameOf(ItemStack stack, DgMaterialComponent<M> materialComponent) {
        DgItemQuality quality = DgItemUtil.qualityOf(stack);
        M material = DgItemUtil.materialOf(stack, materialComponent);
        Text materialText = material == null ? new LiteralText("!! Null Material !!").formatted(Formatting.RED) : new TranslatableText(material.getTranslationKey());
        return formatName(new TranslatableText(stack.getItem().getTranslationKey(), materialText), quality);
    }

    public static Text formatName(Text text, DgItemQuality quality) {
        return text.copy().styled(style -> {
            style = style.withItalic(false);
            if (quality != null) {
                style = style.withColor(quality.getTier().getRarity().formatting);
            }
            return style;
        });
    }

    public static void addLore(ItemStack stack, Text text) {
        CompoundTag display = stack.getOrCreateSubTag("display");
        ListTag loreList;
        if (display.contains("Lore", 9)) {
            loreList = display.getList("Lore", 8);
        } else {
            loreList = new ListTag();
            display.put("Lore", loreList);
        }

        loreList.add(StringTag.of(Text.Serializer.toJson(text)));
    }

    public static void addCustomModel(ItemStack stack, String... modifiers) {
        stack.getOrCreateTag().putInt("CustomModelData", DgItemModelRegistry.getId(modifiers));
    }

    public static <M extends Enum<M> & DgMaterial> void appendMaterialStacks(ItemGroup group, DefaultedList<ItemStack> stacks, DgMaterialComponent<M> materialComponent, BiFunction<M, DgItemQuality, ItemStack> stackFactory) {
        for (DgItemQuality quality : DgItemQuality.values()) {
            if (group == quality.getItemGroup()) {
                for (M material : materialComponent.getMaterials()) {
                    if (quality.ordinal() >= material.getMinQuality().ordinal() && quality.ordinal() <= material.getMaxQuality().ordinal()) {
                        stacks.add(stackFactory.apply(material, quality));
                    }
                }
            }
        }
    }
}
