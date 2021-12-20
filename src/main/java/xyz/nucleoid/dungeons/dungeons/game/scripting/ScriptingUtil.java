package xyz.nucleoid.dungeons.dungeons.game.scripting;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import xyz.nucleoid.dungeons.dungeons.Dungeons;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.TemplateRegion;

import java.util.Random;

public class ScriptingUtil {
    public static Identifier parseDungeonsDefaultId(String str) {
        String[] split = str.split(":");

        if (split.length == 2) {
            return new Identifier(split[0], split[1]);
        } else {
            return new Identifier(Dungeons.ID, split[0]);
        }
    }

    public static BlockBounds getTargetOrDefault(MapTemplate template, TemplateRegion region, NbtCompound data) throws ScriptTemplateInstantiationError {
        if (data.contains("target_region")) {
            String marker = data.getString("target_region");
            BlockBounds target = template.getMetadata().getFirstRegionBounds(marker);

            if (target == null) {
                throw new ScriptTemplateInstantiationError("Invalid target region `" + marker + "`");
            }

            return target;
        } else {
            return region.getBounds();
        }
    }

    public static Vec3d pickRandomBottomCoord(BlockBounds bounds, Random random) {
        BlockPos min = bounds.min();
        BlockPos max = bounds.max();

        double x = MathHelper.nextDouble(random, min.getX(), max.getX());
        double z = MathHelper.nextDouble(random, min.getZ(), max.getZ());
        double y = min.getY();

        return new Vec3d(x, y, z);
    }

    public static void loadChunkAndSpawn(ServerWorld world, Entity entity, Vec3d pos) {
        Chunk chunk = world.getChunk(new BlockPos(pos));
        world.spawnEntity(entity);
        entity.teleport(pos.x, pos.y, pos.z);
    }
}
