package xyz.nucleoid.dungeons.dungeons.game.loot;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.dungeons.dungeons.Dungeons;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class DgModelRegistry {
    private static final Map<Item, LinkedList<String>> REGISTRY = new HashMap<>();
    private static final Object2IntMap<String> IDS = new Object2IntOpenHashMap<>();

    public static void register(Item proxy, String... modifiers) {
        String modifiersString = String.join("/", modifiers);
        REGISTRY.computeIfAbsent(proxy, item -> new LinkedList<>()).add(modifiersString);
        IDS.put(modifiersString, REGISTRY.get(proxy).size() - 1 + 1);
        Dungeons.LOGGER.info("Registering model: " + modifiersString + " as proxy " + Registry.ITEM.getId(proxy).toString() + " with id: " + IDS.getInt(modifiersString));
    }

    public static int getId(String... modifiers) {
        return IDS.getInt(String.join("/", modifiers));
    }

    public static int getId(String modifiers) {
        return IDS.getInt(modifiers);
    }

    public static Map<Item, LinkedList<String>> getRegistry() {
        return REGISTRY;
    }
}
