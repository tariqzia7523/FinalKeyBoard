#include "dictionary/structure/pt_common/shortcut/shortcut_list_reading_utils.h"

#include "dictionary/utils/byte_array_utils.h"

namespace latinime {

// Flag for presence of more attributes
const ShortcutListReadingUtils::ShortcutFlags
        ShortcutListReadingUtils::FLAG_ATTRIBUTE_HAS_NEXT = 0x80;
// Mask for attribute probability, stored on 4 bits inside the flags byte.
const ShortcutListReadingUtils::ShortcutFlags
        ShortcutListReadingUtils::MASK_ATTRIBUTE_PROBABILITY = 0x0F;
const int ShortcutListReadingUtils::SHORTCUT_LIST_SIZE_FIELD_SIZE = 2;
// The numeric value of the shortcut probability that means 'whitelist'.
const int ShortcutListReadingUtils::WHITELIST_SHORTCUT_PROBABILITY = 15;

/* static */ ShortcutListReadingUtils::ShortcutFlags
        ShortcutListReadingUtils::getFlagsAndForwardPointer(const ReadOnlyByteArrayView buffer,
                int *const pos) {
    return ByteArrayUtils::readUint8AndAdvancePosition(buffer.data(), pos);
}

/* static */ int ShortcutListReadingUtils::getShortcutListSizeAndForwardPointer(
        const ReadOnlyByteArrayView buffer, int *const pos) {
    // readUint16andAdvancePosition() returns an offset *including* the uint16 field itself.
    return ByteArrayUtils::readUint16AndAdvancePosition(buffer.data(), pos)
            - SHORTCUT_LIST_SIZE_FIELD_SIZE;
}

/* static */ int ShortcutListReadingUtils::readShortcutTarget(const ReadOnlyByteArrayView buffer,
        const int maxLength, int *const outWord, int *const pos) {
    // TODO: Use codePointTable for shortcuts.
    return ByteArrayUtils::readStringAndAdvancePosition(buffer.data(), maxLength,
            nullptr /* codePointTable */, outWord, pos);
}

} // namespace latinime
