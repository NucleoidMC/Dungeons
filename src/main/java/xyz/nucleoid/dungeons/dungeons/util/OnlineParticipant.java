package xyz.nucleoid.dungeons.dungeons.util;

import net.minecraft.server.network.ServerPlayerEntity;
import xyz.nucleoid.dungeons.dungeons.game.DgPlayer;

public class OnlineParticipant {
    public final DgPlayer participant;
    public final ServerPlayerEntity entity;

    public OnlineParticipant(DgPlayer participant, ServerPlayerEntity entity) {
        this.participant = participant;
        this.entity = entity;
    }
}
