package xyz.nucleoid.dungeons.dungeons.entity.enemy;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.World;

public class DgZombieEntity extends ZombieEntity implements DgEnemy {
    public DgZombieEntity(EntityType<? extends ZombieEntity> entityType, World world) {
        super(entityType, world);
    }

    public DgZombieEntity(World world) {
        super(world);
    }
}
