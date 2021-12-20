package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger;

import xyz.nucleoid.dungeons.dungeons.game.DgActive;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;

import java.util.List;

public interface Action {
    void execute(DgActive active, List<OnlineParticipant> targets);
}
