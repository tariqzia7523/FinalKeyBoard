#include "utils/autocorrection_threshold_utils.h"

#include <gtest/gtest.h>

#include <vector>

namespace latinime {
namespace {

int CalcEditDistance(const std::vector<int> &before,
        const std::vector<int> &after) {
    return AutocorrectionThresholdUtils::editDistance(
            &before[0], before.size(), &after[0], after.size());
}

TEST(AutocorrectionThresholdUtilsTest, SameData) {
    EXPECT_EQ(0, CalcEditDistance({1}, {1}));
    EXPECT_EQ(0, CalcEditDistance({2, 2}, {2, 2}));
    EXPECT_EQ(0, CalcEditDistance({3, 3, 3}, {3, 3, 3}));
}

}  // namespace
}  // namespace latinime
