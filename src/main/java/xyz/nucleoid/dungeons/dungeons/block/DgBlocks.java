package xyz.nucleoid.dungeons.dungeons.block;

import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.dungeons.dungeons.Dungeons;

import java.util.HashMap;
import java.util.Map;

public class DgBlocks {
    private static final Map<Identifier, Block> REGISTRY = new HashMap<>();
    public static Block DESTRUCTIBLE_COBBLESTONE = register("destructible_cobblestone", new DgDestructibleCobblestone());

    private static Block register(String id, Block block) {
        REGISTRY.put(new Identifier(Dungeons.ID, id), block);
        return block;
    }

    public static void register() {
        REGISTRY.forEach((id, block) -> Registry.register(Registry.BLOCK, id, block));
    }
}
