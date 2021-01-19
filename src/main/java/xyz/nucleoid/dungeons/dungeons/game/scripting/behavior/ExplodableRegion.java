package xyz.nucleoid.dungeons.dungeons.game.scripting.behavior;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.plasmid.game.GameOpenException;
import xyz.nucleoid.plasmid.map.template.TemplateRegion;
import xyz.nucleoid.plasmid.util.BlockBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExplodableRegion {
    public final BlockBounds region;
    private final @Nullable List<Block> affectedBlocks;

    public ExplodableRegion(BlockBounds region, @Nullable List<Block> affectedBlocks) {
        this.region = region;
        this.affectedBlocks = affectedBlocks;
    }

    public static ExplodableRegion parse(TemplateRegion region) throws ScriptTemplateInstantiationError {
        CompoundTag data = region.getData();
        List<Block> affectedBlocks = null;

        if (data.contains("affected_blocks")) {
            ListTag list = data.getList("affected_blocks", NbtType.STRING);
            affectedBlocks = new ArrayList<>();

            for (int i = 0; i < list.size(); i++) {
                Identifier id = Identifier.tryParse(list.getString(i));
                Optional<Block> opt = Registry.BLOCK.getOrEmpty(id);
                if (opt.isPresent()) {
                    affectedBlocks.add(opt.get());
                } else {
                    throw new ScriptTemplateInstantiationError("Invalid explodable block id `" + id + "`");
                }
            }
        }

        return new ExplodableRegion(region.getBounds(), affectedBlocks);
    }

    public boolean isExplodable(Block block) {
        if (this.affectedBlocks != null) {
            return this.affectedBlocks.contains(block);
        } else {
            return true;
        }
    }
}
