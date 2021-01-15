package xyz.nucleoid.dungeons.dungeons.game.map.gen.room;

import java.util.Random;

import kdotjpg.opensimplex.OpenSimplexNoise;

import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class SpheroidRoom extends Room {
	private final OpenSimplexNoise noise;
	private final BlockPos center;
	private final int xRadius;
	private final int yRadius;
	private final int zRadius;
	private final BlockBox bounds;

	public SpheroidRoom(Random random, BlockPos center, int xRadius, int yRadius, int zRadius) {
		super(random);
		this.noise = new OpenSimplexNoise(random.nextLong());
		this.center = center;
		this.xRadius = xRadius;
		this.yRadius = yRadius;
		this.zRadius = zRadius;

		this.bounds = new BlockBox(center.getX() - xRadius, center.getY() - yRadius, center.getZ() - zRadius, center.getX() + xRadius, center.getY() + yRadius, center.getZ() + zRadius);
	}

	@Override
	public boolean shouldCarve(BlockPos pos) {
		if (this.bounds.contains(pos)) {
			double xRad = (pos.getX() - this.center.getX()) / (double)this.xRadius;
			double yRad = (pos.getY() - this.center.getY()) / (double)this.yRadius;
			double zRad = (pos.getZ() - this.center.getZ()) / (double)this.zRadius;

			double rad = xRad * xRad + yRad * yRad + zRad * zRad;
			double offset = this.noise.eval(pos.getX() / (double)this.xRadius * 2.0, pos.getZ() / (double)this.yRadius * 2.0, pos.getZ() / (double)this.zRadius * 2.0) * 0.25;

			return rad + offset <= 1;
		}

		return false;
	}

	@Override
	public BlockPos getCenter() {
		return this.center;
	}

	@Override
	public Vec3i getSize() {
		return new Vec3i(this.xRadius, this.yRadius, this.zRadius);
	}
}
