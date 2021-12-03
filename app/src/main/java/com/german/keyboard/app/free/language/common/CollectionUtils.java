package com.german.keyboard.app.free.language.common;

import com.german.keyboard.app.free.inputmethod.annotations.UsedForTesting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility methods for working with collections.
 */
public final class CollectionUtils {
    private CollectionUtils() {
        // This utility class is not publicly instantiable.
    }

    /**
     * Converts a sub-range of the given array to an ArrayList of the appropriate type.
     * @param array Array to be converted.
     * @param start First index inclusive to be converted.
     * @param end Last index exclusive to be converted.
     * @throws IllegalArgumentException if start or end are out of range or start &gt; end.
     */
    @Nonnull
    public static <E> ArrayList<E> arrayAsList(@Nonnull final E[] array, final int start,
            final int end) {
        if (start < 0 || start > end || end > array.length) {
            throw new IllegalArgumentException("Invalid start: " + start + " end: " + end
                    + " with array.length: " + array.length);
        }

        final ArrayList<E> list = new ArrayList<>(end - start);
        for (int i = start; i < end; i++) {
            list.add(array[i]);
        }
        return list;
    }

    /**
     * Tests whether c contains no elements, true if c is null or c is empty.
     * @param c Collection to test.
     * @return Whether c contains no elements.
     */
    @UsedForTesting
    public static boolean isNullOrEmpty(@Nullable final Collection c) {
        return c == null || c.isEmpty();
    }

    /**
     * Tests whether map contains no elements, true if map is null or map is empty.
     * @param map Map to test.
     * @return Whether map contains no elements.
     */
    @UsedForTesting
    public static boolean isNullOrEmpty(@Nullable final Map map) {
        return map == null || map.isEmpty();
    }
}
