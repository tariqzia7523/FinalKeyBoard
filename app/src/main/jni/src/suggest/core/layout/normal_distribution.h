#ifndef LATINIME_NORMAL_DISTRIBUTION_H
#define LATINIME_NORMAL_DISTRIBUTION_H

#include <cmath>

#include "defines.h"

namespace latinime {

// Normal distribution N(u, sigma^2).
class NormalDistribution {
 public:
    NormalDistribution(const float u, const float sigma)
            : mU(u),
              mPreComputedNonExpPart(1.0f / sqrtf(2.0f * M_PI_F
                      * GeometryUtils::SQUARE_FLOAT(sigma))),
              mPreComputedExponentPart(-1.0f / (2.0f * GeometryUtils::SQUARE_FLOAT(sigma))) {}

    float getProbabilityDensity(const float x) const {
        const float shiftedX = x - mU;
        return mPreComputedNonExpPart
                * expf(mPreComputedExponentPart * GeometryUtils::SQUARE_FLOAT(shiftedX));
    }

 private:
    DISALLOW_IMPLICIT_CONSTRUCTORS(NormalDistribution);

    const float mU; // mean value
    const float mPreComputedNonExpPart; // = 1 / sqrt(2 * PI * sigma^2)
    const float mPreComputedExponentPart; // = -1 / (2 * sigma^2)
};
} // namespace latinime
#endif // LATINIME_NORMAL_DISTRIBUTION_H
