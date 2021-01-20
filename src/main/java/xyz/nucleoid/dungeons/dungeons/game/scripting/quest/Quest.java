package xyz.nucleoid.dungeons.dungeons.game.scripting.quest;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import xyz.nucleoid.plasmid.widget.SidebarWidget;

import java.util.Map;

public class Quest {
    public final String id;
    protected final Object2ObjectMap<String, QuestObjective> finishedObjectives;
    protected final Object2ObjectMap<String, QuestObjective> currentObjectives;

    // Whether this quest could need re-rendering to the scoreboard
    protected boolean dirty = true;

    public Quest(String id) {
        this.id = id;
        this.currentObjectives = new Object2ObjectOpenHashMap<>();
        this.finishedObjectives = new Object2ObjectOpenHashMap<>();
    }

    public void advanceTo(String id, QuestObjective objective) throws QuestException {
        this.finishedObjectives.putAll(this.currentObjectives);

        if (this.finishedObjectives.containsKey(id)) {
            throw new QuestException("Attempted to insert new quest objective with id `" + id + "`, but it already exists in quest `" + this.id + "`");
        }

        this.currentObjectives.clear();
        this.currentObjectives.put(id, objective);
        this.dirty = true;
    }

    public void renderToSidebar(SidebarWidget.Content content) {
        // TODO(quests): ordered objectives? are they needed?
        this.currentObjectives.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .forEach(obj -> obj.format(this, content));
    }
}
