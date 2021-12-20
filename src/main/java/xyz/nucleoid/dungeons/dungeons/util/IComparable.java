package xyz.nucleoid.dungeons.dungeons.util;

/**
 * Composable interface that provides utility functions for comparables.
 * @param <T> The type you compare it to.
 */
public interface IComparable<T> extends Comparable<T> {
    default boolean greaterThan(T other) {
        return compareTo(other) > 0;
    }

    default boolean lessThan(T other) {
        return compareTo(other) < 0;
    }

    default boolean greaterOrEquals(T other) {
        return compareTo(other) >= 0;
    }

    default boolean lessOrEquals(T other) {
        return compareTo(other) <= 0;
    }
}
