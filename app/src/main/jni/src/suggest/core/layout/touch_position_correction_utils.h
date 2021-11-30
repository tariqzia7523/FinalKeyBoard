#ifndef LATINIME_TOUCH_POSITION_CORRECTION_UTILS_H
#define LATINIME_TOUCH_POSITION_CORRECTION_UTILS_H

#include <algorithm>

#include "defines.h"
#include "suggest/core/layout/proximity_info_params.h"

namespace latinime {
class TouchPositionCorrectionUtils {
 public:
    static float getSweetSpotFactor(const bool isTouchPositionCorrectionEnabled,
            const float normalizedSquaredDistance) {
        // Promote or demote the score according to the distance from the sweet spot
        static const float A = 0.0f;
        static const float B = 0.24f;
        static const float C = 1.20f;
        static const float R0 = 0.0f;
        static const float R1 = 0.25f; // Sweet spot
        static const float R2 = 1.0f;
        const float x = normalizedSquaredDistance;
        if (!isTouchPositionCorrectionEnabled) {
            return std::min(C, x);
        }

        // factor is a piecewise linear function like:
        // C        -------------.
        //         /             .
        // B      /              .
        //      -/               .
        // A _-^                 .
        //                       .
        //   R0 R1 R2            .

        if (x < R0) {
            return A;
        } else if (x < R1) {
            return (A * (R1 - x) + B * (x - R0)) / (R1 - R0);
        } else if (x < R2) {
            return (B * (R2 - x) + C * (x - R1)) / (R2 - R1);
        } else {
            return C;
        }
    }
 private:
    DISALLOW_IMPLICIT_CONSTRUCTORS(TouchPositionCorrectionUtils);
};
} // namespace latinime
#endif // LATINIME_TOUCH_POSITION_CORRECTION_UTILS_H
