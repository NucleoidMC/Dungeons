package xyz.nucleoid.dungeons.dungeons.game.scripting.quest;

public class QuestException extends Exception {
    public final String reason;

    public QuestException(String reason) {
        this.reason = reason;
    }
}
