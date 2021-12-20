package xyz.nucleoid.dungeons.dungeons.game.scripting.enemy_spawn;

import net.minecraft.server.world.ServerWorld;

public interface EnemySpawner {
    void spawn(ServerWorld world);
}
