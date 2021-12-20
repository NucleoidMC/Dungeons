package xyz.nucleoid.dungeons.dungeons.assets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.dungeons.dungeons.item.DgItemUtil;
import xyz.nucleoid.dungeons.dungeons.item.model.DgItemModelRegistry;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Map;

// Todo: make better
public class DgModelGenerator {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void generateModels() {
        File out = new File(FabricLoader.getInstance().getGameDir().toFile(), "out");
        File dungeonsItemModelOut = new File(out, "assets/dungeons/models/item");
        File minecraftItemModelOut = new File(out, "assets/minecraft/models/item");
        Map<Item, LinkedList<String>> registry = DgItemModelRegistry.getRegistry();
        registry.forEach((proxy, items) -> {
            JsonArray overrides = new JsonArray();
            items.forEach(modifiersString -> {
                String[] modifiers = modifiersString.split("/");
                File dir = dungeonsItemModelOut;
                for (int i = 0; i < modifiers.length - 1; i++) {
                    dir = new File(dir, modifiers[i]);
                }
                dir.mkdirs();
                JsonObject object = new JsonObject();
                object.addProperty("parent", "minecraft:item/handheld");
                JsonObject textures = new JsonObject();
                textures.addProperty("layer0", "dungeons:item/" + modifiersString);
                object.add("textures", textures);
                File modelFile = new File(dir, modifiers[modifiers.length - 1] + ".json");
                try {
                    BufferedWriter writer = Files.newBufferedWriter(modelFile.toPath());
                    writer.write(GSON.toJson(object));
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JsonObject override = new JsonObject();
                JsonObject predicate = new JsonObject();
                predicate.addProperty("custom_model_data", DgItemModelRegistry.getId(modifiersString));
                override.add("predicate", predicate);
                override.addProperty("model", "dungeons:item/" + modifiersString);
                overrides.add(override);
            });
            JsonObject object = new JsonObject();
            object.addProperty("parent", "minecraft:item/handheld"); // Todo: Make work with bows and crossbows (this is ugly lol)
            JsonObject textures = new JsonObject();
            String proxyName = Registry.ITEM.getId(proxy).getPath();
            textures.addProperty("layer0", "minecraft:item/" + proxyName);
            object.add("textures", textures);
            object.add("overrides", overrides);
            minecraftItemModelOut.mkdirs();
            File minecraftModelFile = new File(minecraftItemModelOut, proxyName + ".json");
            try {
                BufferedWriter writer = Files.newBufferedWriter(minecraftModelFile.toPath());
                writer.write(GSON.toJson(object));
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // These are added for debug purposes to view in the creative menu (when viewing the real item)
            for (Item dungeonsItem : DgItemModelRegistry.getItemsWithProxy(proxy)) {
                File dungeonsModelFile = new File(dungeonsItemModelOut, DgItemUtil.idPathOf(dungeonsItem) + ".json");
                try {
                    BufferedWriter writer = Files.newBufferedWriter(dungeonsModelFile.toPath());
                    writer.write(GSON.toJson(object));
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
