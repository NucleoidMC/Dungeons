package xyz.nucleoid.dungeons.dungeons.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

public class EnumArgumentType<T extends Enum<T>> implements ArgumentType<T> {
    private Class<T> clazz;

    public EnumArgumentType(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final String result = reader.readString();
        for (T value : clazz.getEnumConstants()) {
            if (result.equals(getSuggestionString(value))) {
                return value;
            }
        }
        reader.setCursor(start);
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (T value : clazz.getEnumConstants()) {
            builder.suggest(getSuggestionString(value));
        }
        return CompletableFuture.completedFuture(builder.build());
    }

    private String getSuggestionString(T value) {
        if (value instanceof DisplayName displayName) {
            return displayName.getDisplayName();
        } else {
            return value.name();
        }
    }

    public static <T> T getValue(final CommandContext<?> context, final String name, final Class<T> clazz) {
        return context.getArgument(name, clazz);
    }
}
