#ifndef LATINIME_SUGGEST_OPTIONS_H
#define LATINIME_SUGGEST_OPTIONS_H

#include "defines.h"

namespace latinime {

class SuggestOptions{
 public:
    SuggestOptions(const int *const options, const int length)
            : mOptions(options), mLength(length) {}

    AK_FORCE_INLINE bool isGesture() const {
        return getBoolOption(IS_GESTURE);
    }

    AK_FORCE_INLINE bool useFullEditDistance() const {
        return getBoolOption(USE_FULL_EDIT_DISTANCE);
    }

    AK_FORCE_INLINE bool blockOffensiveWords() const {
        return getBoolOption(BLOCK_OFFENSIVE_WORDS);
    }

    AK_FORCE_INLINE bool enableSpaceAwareGesture() const {
        return getBoolOption(SPACE_AWARE_GESTURE_ENABLED);
    }

    AK_FORCE_INLINE float weightForLocale() const {
        // The weight is in thousands and we want the real value, so we divide by 1000.
        // NativeSuggestOptions#setWeightForLocale does the opposite processing in Java.
        return static_cast<float>(getIntOption(WEIGHT_FOR_LOCALE_IN_THOUSANDS)) / 1000.0f;
    }

    AK_FORCE_INLINE bool getAdditionalFeaturesBoolOption(const int key) const {
        return getBoolOption(key + ADDITIONAL_FEATURES_OPTIONS);
    }

 private:
    DISALLOW_IMPLICIT_CONSTRUCTORS(SuggestOptions);

    // Need to update com.german.keyboard.app.free.NativeSuggestOptions when you add, remove or
    // reorder options.
    static const int IS_GESTURE = 0;
    static const int USE_FULL_EDIT_DISTANCE = 1;
    static const int BLOCK_OFFENSIVE_WORDS = 2;
    static const int SPACE_AWARE_GESTURE_ENABLED = 3;
    static const int WEIGHT_FOR_LOCALE_IN_THOUSANDS = 4;
    // Additional features options are stored after the other options and used as setting values of
    // experimental features.
    static const int ADDITIONAL_FEATURES_OPTIONS = 5;

    const int *const mOptions;
    const int mLength;

    AK_FORCE_INLINE bool isValidKey(const int key) const {
        return 0 <= key && key < mLength;
    }

    AK_FORCE_INLINE bool getBoolOption(const int key) const {
        if (isValidKey(key)) {
            return mOptions[key] != 0;
        }
        return false;
    }

    AK_FORCE_INLINE int getIntOption(const int key) const {
        if (isValidKey(key)) {
            return mOptions[key];
        }
        return 0;
    }
};
} // namespace latinime
#endif // LATINIME_SUGGEST_OPTIONS_H
