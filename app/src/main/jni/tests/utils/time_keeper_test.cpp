#include "utils/time_keeper.h"

#include <gtest/gtest.h>

namespace latinime {
namespace {

TEST(TimeKeeperTest, TestTestMode) {
    TimeKeeper::setCurrentTime();
    const int startTime = TimeKeeper::peekCurrentTime();
    static const int TEST_CURRENT_TIME = 100;
    TimeKeeper::startTestModeWithForceCurrentTime(TEST_CURRENT_TIME);
    EXPECT_EQ(TEST_CURRENT_TIME, TimeKeeper::peekCurrentTime());
    TimeKeeper::setCurrentTime();
    EXPECT_EQ(TEST_CURRENT_TIME, TimeKeeper::peekCurrentTime());
    TimeKeeper::stopTestMode();
    TimeKeeper::setCurrentTime();
    EXPECT_LE(startTime, TimeKeeper::peekCurrentTime());
}

}  // namespace
}  // namespace latinime
