package xyz.nucleoid.dungeons.dungeons.game.scripting;

// An error instantiating some script template, e.g a spawner, action, or trigger criterion.
public class ScriptTemplateInstantiationError extends Exception {
    public final String reason;

    public ScriptTemplateInstantiationError(String reason) {
        this.reason = reason;
    }
}
