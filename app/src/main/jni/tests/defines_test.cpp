#include "defines.h"

#include <gtest/gtest.h>

namespace latinime {
namespace {

TEST(DefinesTest, NELEMSForFixedLengthArray) {
    const size_t SMALL_ARRAY_SIZE = 1;
    const size_t LARGE_ARRAY_SIZE = 100;
    int smallArray[SMALL_ARRAY_SIZE];
    int largeArray[LARGE_ARRAY_SIZE];
    EXPECT_EQ(SMALL_ARRAY_SIZE, NELEMS(smallArray));
    EXPECT_EQ(LARGE_ARRAY_SIZE, NELEMS(largeArray));
}

}  // namespace
}  // namespace latinime
