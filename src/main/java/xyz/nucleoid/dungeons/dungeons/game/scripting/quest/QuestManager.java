package xyz.nucleoid.dungeons.dungeons.game.scripting.quest;

import com.mojang.serialization.Lifecycle;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.Dungeons;
import xyz.nucleoid.dungeons.dungeons.game.DgActive;
import xyz.nucleoid.dungeons.dungeons.game.scripting.quest.objectives.SimpleObjective;
import xyz.nucleoid.plasmid.registry.TinyRegistry;
import xyz.nucleoid.plasmid.util.PlayerRef;
import xyz.nucleoid.plasmid.widget.SidebarWidget;

import java.util.Map;
import java.util.Objects;

public class QuestManager {
    public static final TinyRegistry<QuestObjectiveBuilder> OBJECTIVE_BUILDERS = new TinyRegistry<>(Lifecycle.stable());
    private @Nullable Quest quest;
    private @Nullable SidebarWidget sidebar;
    // Whether the current quest has changed and must be rendered
    private boolean dirty = false;

    static {
        register("simple", SimpleObjective::create);
    }

    private static void register(String id, QuestObjectiveBuilder builder) {
        OBJECTIVE_BUILDERS.register(new Identifier(Dungeons.ID, id), builder);
    }

    public void tick(DgActive active) {
        if (this.quest != null && (this.dirty || this.quest.dirty)) {
            if (this.sidebar == null) {
                Text text = new TranslatableText("quest.dungeons." + this.quest.id + ".name").formatted(Formatting.BOLD, Formatting.BLUE);
                SidebarWidget sidebar = active.widgets.addSidebar(text);
                this.sidebar = sidebar;

                for (PlayerRef player : active.participants.keySet()) {
                    player.ifOnline(active.gameSpace.getWorld(), sidebar::addPlayer);
                }
            }

            Objects.requireNonNull(this.sidebar).set(this.quest::renderToSidebar);
            this.dirty = false;
            this.quest.dirty = false;
        }
    }

    public void setCurrentQuest(@NotNull Quest current) {
        if (this.quest != null) {
            throw new IllegalStateException("There is already a quest active");
        }

        this.quest = current;
        this.dirty = true;
    }

    public Quest getCurrentQuest() {
        return this.quest;
    }
}
