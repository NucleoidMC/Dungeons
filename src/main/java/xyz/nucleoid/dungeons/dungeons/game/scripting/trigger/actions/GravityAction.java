package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.actions;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.game.DgActive;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.Action;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.TriggerInstantiationError;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.map.template.TemplateRegion;
import xyz.nucleoid.plasmid.util.BlockBounds;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GravityAction implements Action {
    private final BlockBounds targetRegion;
    private final @Nullable List<Block> affectedBlocks;
    private final DropBehaviour dropBehaviour;
    private final boolean destroyItemFrames;

    public GravityAction(BlockBounds targetRegion, @Nullable List<Block> affectedBlocks, boolean destroyItemFrames, DropBehaviour dropBehaviour) {
        this.targetRegion = targetRegion;
        this.affectedBlocks = affectedBlocks;
        this.dropBehaviour = dropBehaviour;
        this.destroyItemFrames = destroyItemFrames;
    }

    public static GravityAction create(MapTemplate template, TemplateRegion trigger, CompoundTag data) throws TriggerInstantiationError {
        BlockBounds target;
        if (data.contains("target_region")) {
            String marker = data.getString("target_region");
            target = template.getMetadata().getFirstRegionBounds(marker);

            if (target == null) {
                throw new TriggerInstantiationError("Invalid target region `" + marker + "`");
            }
        } else {
            target = trigger.getBounds();
        }

        List<Block> affectedBlocks = null;
        if (data.contains("affected_blocks")) {
            affectedBlocks = new ArrayList<>();
            ListTag list = data.getList("affected_blocks", NbtType.STRING);

            for (int i = 0; i < list.size(); i++) {
                Identifier id = Identifier.tryParse(list.getString(i));

                if (id == null || !Registry.BLOCK.getOrEmpty(id).isPresent()) {
                    throw new TriggerInstantiationError("Invalid block `" + list.getString(i) + "`");
                }

                affectedBlocks.add(Registry.BLOCK.get(id));
            }
        }

        DropBehaviour dropBehaviour = DropBehaviour.DISAPPEAR;
        if (data.contains("drop_behaviour")) {
            switch (data.getString("drop_behaviour")) {
                case "drop_item":
                    dropBehaviour = DropBehaviour.DROP_ITEM;
                    break;
                case "place_block":
                    dropBehaviour = DropBehaviour.PLACE_BLOCK;
                    break;
                case "disappear":
                    break;
                default:
                    throw new TriggerInstantiationError("Invalid drop behaviour `" + data.getString("drop_behaviour") + "`");
            }
        }

        boolean destroyItemFrames = true;
        if (data.contains("remove_item_frames")) {
            destroyItemFrames = data.getBoolean("remove_item_frames");
        }

        return new GravityAction(target, affectedBlocks, destroyItemFrames, dropBehaviour);
    }

    @Override
    public void execute(DgActive game, List<OnlineParticipant> targets) {
        double blockFallVelocity = targets.stream()
                .map(p -> p.entity.getVelocity().getComponentAlongAxis(Direction.Axis.Y))
                .min(Comparator.comparingDouble(y -> y))
                .orElse(0.0);
        blockFallVelocity = Math.min(0.0, blockFallVelocity);

        ServerWorld world = game.gameSpace.getWorld();
        for (BlockPos pos : this.targetRegion) {
            BlockState state = world.getBlockState(pos);

            if (this.affectedBlocks != null && !this.affectedBlocks.contains(state.getBlock())) {
                continue;
            }

            FallingBlockEntity fallingBlock = new FallingBlockEntity(world, pos.getX(), pos.getY(), pos.getZ(), state);
            fallingBlock.timeFalling = 1;

            switch (this.dropBehaviour) {
                case DROP_ITEM:
                    fallingBlock.dropItem = true;
                    break;
                case PLACE_BLOCK:
                    fallingBlock.dropItem = false;
                    break;
                case DISAPPEAR:
                    fallingBlock.dropItem = false;
                    fallingBlock.destroyedOnLanding = true;
                    break;
            }

            fallingBlock.addVelocity(0.0, blockFallVelocity, 0.0);
            world.spawnEntity(fallingBlock);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }

        if (this.destroyItemFrames) {
            for (ItemFrameEntity entity : world.getEntitiesByType(EntityType.ITEM_FRAME, this.targetRegion.toBox(), p -> true)) {
                entity.remove();
            }
        }
    }

    enum DropBehaviour {
        DROP_ITEM,
        PLACE_BLOCK,
        DISAPPEAR
    }
}
