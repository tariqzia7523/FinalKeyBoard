#ifndef LATINIME_AUTOCORRECTION_THRESHOLD_UTILS_H
#define LATINIME_AUTOCORRECTION_THRESHOLD_UTILS_H

#include "defines.h"

namespace latinime {

class AutocorrectionThresholdUtils {
 public:
    static float calcNormalizedScore(const int *before, const int beforeLength,
            const int *after, const int afterLength, const int score);
    static int editDistance(const int *before, const int beforeLength, const int *after,
            const int afterLength);

 private:
    DISALLOW_IMPLICIT_CONSTRUCTORS(AutocorrectionThresholdUtils);

    static const int MAX_INITIAL_SCORE;
    static const int TYPED_LETTER_MULTIPLIER;
    static const int FULL_WORD_MULTIPLIER;
};
} // namespace latinime
#endif // LATINIME_AUTOCORRECTION_THRESHOLD_UTILS_H
