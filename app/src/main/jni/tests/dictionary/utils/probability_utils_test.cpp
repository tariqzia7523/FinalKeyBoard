#include "dictionary/utils/probability_utils.h"

#include <gtest/gtest.h>

#include "defines.h"

namespace latinime {
namespace {

TEST(ProbabilityUtilsTest, TestEncodeRawProbability) {
    EXPECT_EQ(MAX_PROBABILITY, ProbabilityUtils::encodeRawProbability(1.0f));
    EXPECT_EQ(MAX_PROBABILITY - 9, ProbabilityUtils::encodeRawProbability(0.5f));
    EXPECT_EQ(0, ProbabilityUtils::encodeRawProbability(0.0f));
}

}  // namespace
}  // namespace latinime
