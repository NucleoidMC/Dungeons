package xyz.nucleoid.dungeons.dungeons.game.scripting.quest;

import xyz.nucleoid.plasmid.widget.SidebarWidget;

public interface QuestObjective {
    void format(Quest quest, SidebarWidget.Content content);
}
