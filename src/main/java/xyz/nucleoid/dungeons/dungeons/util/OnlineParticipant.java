package xyz.nucleoid.dungeons.dungeons.util;

import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.dungeons.dungeons.game.DgPlayer;

public record OnlineParticipant(DgPlayer participant, ServerPlayerEntity entity) { }
