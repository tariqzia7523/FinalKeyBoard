#ifndef LATINIME_SHORTCUT_LIST_READING_UTILS_H
#define LATINIME_SHORTCUT_LIST_READING_UTILS_H

#include <cstdint>

#include "defines.h"
#include "utils/byte_array_view.h"

namespace latinime {

class ShortcutListReadingUtils {
 public:
    typedef uint8_t ShortcutFlags;

    static ShortcutFlags getFlagsAndForwardPointer(const ReadOnlyByteArrayView buffer,
            int *const pos);

    static AK_FORCE_INLINE int getProbabilityFromFlags(const ShortcutFlags flags) {
        return flags & MASK_ATTRIBUTE_PROBABILITY;
    }

    static AK_FORCE_INLINE bool hasNext(const ShortcutFlags flags) {
        return (flags & FLAG_ATTRIBUTE_HAS_NEXT) != 0;
    }

    // This method returns the size of the shortcut list region excluding the shortcut list size
    // field at the beginning.
    static int getShortcutListSizeAndForwardPointer(const ReadOnlyByteArrayView buffer,
            int *const pos);

    static AK_FORCE_INLINE int getShortcutListSizeFieldSize() {
        return SHORTCUT_LIST_SIZE_FIELD_SIZE;
    }

    static AK_FORCE_INLINE void skipShortcuts(const ReadOnlyByteArrayView buffer, int *const pos) {
        const int shortcutListSize = getShortcutListSizeAndForwardPointer(buffer, pos);
        *pos += shortcutListSize;
    }

    static AK_FORCE_INLINE bool isWhitelist(const ShortcutFlags flags) {
        return getProbabilityFromFlags(flags) == WHITELIST_SHORTCUT_PROBABILITY;
    }

    static int readShortcutTarget(const ReadOnlyByteArrayView buffer, const int maxLength,
            int *const outWord, int *const pos);

 private:
    DISALLOW_IMPLICIT_CONSTRUCTORS(ShortcutListReadingUtils);

    static const ShortcutFlags FLAG_ATTRIBUTE_HAS_NEXT;
    static const ShortcutFlags MASK_ATTRIBUTE_PROBABILITY;
    static const int SHORTCUT_LIST_SIZE_FIELD_SIZE;
    static const int WHITELIST_SHORTCUT_PROBABILITY;
};
} // namespace latinime
#endif // LATINIME_SHORTCUT_LIST_READING_UTILS_H
