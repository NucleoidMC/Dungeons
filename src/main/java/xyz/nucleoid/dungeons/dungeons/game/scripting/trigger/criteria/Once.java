package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.criteria;

import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.TriggerCriterion;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;

import java.util.List;

public class Once implements TriggerCriterion {
    @Override
    public TestResult testForPlayers(List<OnlineParticipant> playersInside) {
        return new TestResult(playersInside, true);
    }
}
