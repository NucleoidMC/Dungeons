package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger;

import xyz.nucleoid.dungeons.dungeons.game.DgActive;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;

import java.util.List;

public interface TriggerCriterion {
    TestResult testForPlayers(DgActive active, List<OnlineParticipant> playersInside);

    record TestResult(List<OnlineParticipant> runsFor,
                      boolean removeTrigger) {
    }
}
