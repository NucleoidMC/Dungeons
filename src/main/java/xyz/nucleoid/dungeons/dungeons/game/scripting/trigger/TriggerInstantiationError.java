package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger;

public class TriggerInstantiationError extends Exception {
    public final String reason;

    public TriggerInstantiationError(String reason) {
        this.reason = reason;
    }
}
