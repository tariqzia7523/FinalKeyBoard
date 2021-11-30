#ifndef LATINIME_SHORTCUT_LIST_POLICY_H
#define LATINIME_SHORTCUT_LIST_POLICY_H

#include <cstdint>

#include "defines.h"
#include "dictionary/interface/dictionary_shortcuts_structure_policy.h"
#include "dictionary/structure/pt_common/shortcut/shortcut_list_reading_utils.h"
#include "utils/byte_array_view.h"

namespace latinime {

class ShortcutListPolicy : public DictionaryShortcutsStructurePolicy {
 public:
    explicit ShortcutListPolicy(const ReadOnlyByteArrayView buffer) : mBuffer(buffer) {}

    ~ShortcutListPolicy() {}

    int getStartPos(const int pos) const {
        if (pos == NOT_A_DICT_POS) {
            return NOT_A_DICT_POS;
        }
        int listPos = pos;
        ShortcutListReadingUtils::getShortcutListSizeAndForwardPointer(mBuffer, &listPos);
        return listPos;
    }

    void getNextShortcut(const int maxCodePointCount, int *const outCodePoint,
            int *const outCodePointCount, bool *const outIsWhitelist, bool *const outHasNext,
            int *const pos) const {
        const ShortcutListReadingUtils::ShortcutFlags flags =
                ShortcutListReadingUtils::getFlagsAndForwardPointer(mBuffer, pos);
        if (outHasNext) {
            *outHasNext = ShortcutListReadingUtils::hasNext(flags);
        }
        if (outIsWhitelist) {
            *outIsWhitelist = ShortcutListReadingUtils::isWhitelist(flags);
        }
        if (outCodePoint) {
            *outCodePointCount = ShortcutListReadingUtils::readShortcutTarget(
                    mBuffer, maxCodePointCount, outCodePoint, pos);
        }
    }

    void skipAllShortcuts(int *const pos) const {
        const int shortcutListSize = ShortcutListReadingUtils
                ::getShortcutListSizeAndForwardPointer(mBuffer, pos);
        *pos += shortcutListSize;
    }

 private:
    DISALLOW_IMPLICIT_CONSTRUCTORS(ShortcutListPolicy);

    const ReadOnlyByteArrayView mBuffer;
};
} // namespace latinime
#endif // LATINIME_SHORTCUT_LIST_POLICY_H
