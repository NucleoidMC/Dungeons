package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.actions;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.game.DgActive;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptingUtil;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.Action;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.TemplateRegion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public record GravityAction(BlockBounds targetRegion,
                            @Nullable List<Block> affectedBlocks,
                            boolean destroyItemFrames,
                            xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.actions.GravityAction.DropBehaviour dropBehaviour) implements Action {
    public GravityAction(BlockBounds targetRegion, @Nullable List<Block> affectedBlocks, boolean destroyItemFrames, DropBehaviour dropBehaviour) {
        this.targetRegion = targetRegion;
        this.affectedBlocks = affectedBlocks;
        this.dropBehaviour = dropBehaviour;
        this.destroyItemFrames = destroyItemFrames;
    }

    public static GravityAction create(MapTemplate template, TemplateRegion trigger, NbtCompound data) throws ScriptTemplateInstantiationError {
        BlockBounds target = ScriptingUtil.getTargetOrDefault(template, trigger, data);

        List<Block> affectedBlocks = null;
        if (data.contains("affected_blocks")) {
            affectedBlocks = new ArrayList<>();
            NbtList list = data.getList("affected_blocks", NbtType.STRING);

            for (int i = 0; i < list.size(); i++) {
                Identifier id = Identifier.tryParse(list.getString(i));

                if (id == null || Registry.BLOCK.getOrEmpty(id).isEmpty()) {
                    throw new ScriptTemplateInstantiationError("Invalid block `" + list.getString(i) + "`");
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
                    throw new ScriptTemplateInstantiationError("Invalid drop behaviour `" + data.getString("drop_behaviour") + "`");
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
                .map(p -> p.entity().getVelocity().getComponentAlongAxis(Direction.Axis.Y))
                .min(Comparator.comparingDouble(y -> y))
                .orElse(0.0);
        blockFallVelocity = Math.min(0.0, blockFallVelocity);

        ServerWorld world = game.world;
        for (BlockPos pos : this.targetRegion) {
            BlockState state = world.getBlockState(pos);

            if (this.affectedBlocks != null && !this.affectedBlocks.contains(state.getBlock())) {
                continue;
            }

            FallingBlockEntity fallingBlock = new FallingBlockEntity(world, pos.getX(), pos.getY(), pos.getZ(), state);
            fallingBlock.timeFalling = 1;

            switch (this.dropBehaviour) {
                case DROP_ITEM -> fallingBlock.dropItem = true;
                case PLACE_BLOCK -> fallingBlock.dropItem = false;
                case DISAPPEAR -> {
                    fallingBlock.dropItem = false;
                    fallingBlock.destroyedOnLanding = true;
                }
            }

            fallingBlock.addVelocity(0.0, blockFallVelocity, 0.0);
            world.spawnEntity(fallingBlock);
            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }

        if (this.destroyItemFrames) {
            for (ItemFrameEntity entity : world.getEntitiesByType(EntityType.ITEM_FRAME, this.targetRegion.asBox(), p -> true)) {
                entity.kill(); // TODO?
            }
        }
    }

    enum DropBehaviour {
        DROP_ITEM,
        PLACE_BLOCK,
        DISAPPEAR
    }
}
