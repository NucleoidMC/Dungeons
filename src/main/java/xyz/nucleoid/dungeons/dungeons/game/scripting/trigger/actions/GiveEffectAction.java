package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.actions;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.dungeons.dungeons.game.DgActive;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.Action;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.TemplateRegion;

import java.util.List;

public record GiveEffectAction(StatusEffect effect, int level,
                               int duration_secs) implements Action {

    public static GiveEffectAction create(MapTemplate template, TemplateRegion trigger, NbtCompound data) throws ScriptTemplateInstantiationError {
        if (!data.contains("effect")) {
            throw new ScriptTemplateInstantiationError("Effect is a required argument for dungeons:effect");
        }

        Identifier id = Identifier.tryParse(data.getString("effect"));

        if (id == null || Registry.STATUS_EFFECT.getOrEmpty(id).isEmpty()) {
            throw new ScriptTemplateInstantiationError("Invalid effect `" + data.getString("effect") + "`");
        }

        StatusEffect effect = Registry.STATUS_EFFECT.get(id);

        int level = 0;
        if (data.contains("level")) {
            level = data.getInt("level");
        }

        int duration_secs = 10;
        if (data.contains("duration_secs")) {
            duration_secs = data.getInt("duration_secs");
        }

        return new GiveEffectAction(effect, level, duration_secs);
    }

    @Override
    public void execute(DgActive active, List<OnlineParticipant> targets) {
        for (OnlineParticipant participant : targets) {
            participant.entity().addStatusEffect(new StatusEffectInstance(
                    this.effect,
                    this.duration_secs * 20,
                    this.level,
                    true,
                    false,
                    false
            ));
        }
    }
}
