#ifndef LATINIME_BINARY_DICTIONARY_SHORTCUT_ITERATOR_H
#define LATINIME_BINARY_DICTIONARY_SHORTCUT_ITERATOR_H

#include "defines.h"
#include "dictionary/interface/dictionary_shortcuts_structure_policy.h"

namespace latinime {

class BinaryDictionaryShortcutIterator {
 public:
    BinaryDictionaryShortcutIterator(
            const DictionaryShortcutsStructurePolicy *const shortcutStructurePolicy,
            const int shortcutPos)
            : mShortcutStructurePolicy(shortcutStructurePolicy),
              mPos(shortcutStructurePolicy->getStartPos(shortcutPos)),
              mHasNextShortcutTarget(shortcutPos != NOT_A_DICT_POS) {}

    BinaryDictionaryShortcutIterator(const BinaryDictionaryShortcutIterator &&shortcutIterator) noexcept
            : mShortcutStructurePolicy(shortcutIterator.mShortcutStructurePolicy),
              mPos(shortcutIterator.mPos),
              mHasNextShortcutTarget(shortcutIterator.mHasNextShortcutTarget) {}

    AK_FORCE_INLINE bool hasNextShortcutTarget() const {
        return mHasNextShortcutTarget;
    }

    // Gets the shortcut target itself as an int string and put it to outTarget, put its length
    // to outTargetLength, put whether it is whitelist to outIsWhitelist.
    AK_FORCE_INLINE void nextShortcutTarget(
            const int maxDepth, int *const outTarget, int *const outTargetLength,
            bool *const outIsWhitelist) {
        mShortcutStructurePolicy->getNextShortcut(maxDepth, outTarget, outTargetLength,
                outIsWhitelist, &mHasNextShortcutTarget, &mPos);
    }

 private:
    DISALLOW_DEFAULT_CONSTRUCTOR(BinaryDictionaryShortcutIterator);
    DISALLOW_ASSIGNMENT_OPERATOR(BinaryDictionaryShortcutIterator);

    const DictionaryShortcutsStructurePolicy *const mShortcutStructurePolicy;
    int mPos;
    bool mHasNextShortcutTarget;
};
} // namespace latinime
#endif // LATINIME_BINARY_DICTIONARY_SHORTCUT_ITERATOR_H
