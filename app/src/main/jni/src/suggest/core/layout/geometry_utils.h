#ifndef LATINIME_GEOMETRY_UTILS_H
#define LATINIME_GEOMETRY_UTILS_H

#include <cmath>

#include "defines.h"

#define ROUND_FLOAT_10000(f) ((f) < 1000.0f && (f) > 0.001f) \
        ? (floorf((f) * 10000.0f) / 10000.0f) : (f)

namespace latinime {

class GeometryUtils {
 public:
    static inline float SQUARE_FLOAT(const float x) { return x * x; }

    static AK_FORCE_INLINE float getAngle(const int x1, const int y1, const int x2, const int y2) {
        const int dx = x1 - x2;
        const int dy = y1 - y2;
        if (dx == 0 && dy == 0) return 0.0f;
        return atan2f(static_cast<float>(dy), static_cast<float>(dx));
    }

    static AK_FORCE_INLINE float getAngleDiff(const float a1, const float a2) {
        static const float M_2PI_F = M_PI * 2.0f;
        float delta = fabsf(a1 - a2);
        if (delta > M_2PI_F) {
            delta -= (M_2PI_F * static_cast<int>(delta / M_2PI_F));
        }
        if (delta > M_PI_F) {
            delta = M_2PI_F - delta;
        }
        return ROUND_FLOAT_10000(delta);
    }

    static AK_FORCE_INLINE int getDistanceInt(const int x1, const int y1, const int x2,
            const int y2) {
        return static_cast<int>(hypotf(static_cast<float>(x1 - x2), static_cast<float>(y1 - y2)));
    }

 private:
    DISALLOW_IMPLICIT_CONSTRUCTORS(GeometryUtils);
};
} // namespace latinime
#endif // LATINIME_GEOMETRY_UTILS_H
