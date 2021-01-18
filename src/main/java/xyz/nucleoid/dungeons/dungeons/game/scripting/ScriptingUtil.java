package xyz.nucleoid.dungeons.dungeons.game.scripting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.map.template.TemplateRegion;
import xyz.nucleoid.plasmid.util.BlockBounds;

import java.util.Random;

public class ScriptingUtil {
    public static Identifier parseDungeonsDefaultId(String str) {
        String[] split = str.split(":");

        if (split.length == 2) {
            return new Identifier(split[0], split[1]);
        } else {
            return new Identifier("dungeons", split[0]);
        }
    }

    public static BlockBounds getTargetOrDefault(MapTemplate template, TemplateRegion region, CompoundTag data) throws ScriptTemplateInstantiationError {
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
        BlockPos min = bounds.getMin();
        BlockPos max = bounds.getMax();

        double x = MathHelper.nextDouble(random, min.getX(), max.getX());
        double z = MathHelper.nextDouble(random, min.getZ(), max.getZ());
        double y = min.getY();

        return new Vec3d(x, y, z);
    }
}
