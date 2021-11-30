#include "dictionary/structure/v4/content/language_model_dict_content_global_counters.h"

#include <gtest/gtest.h>

#include "dictionary/structure/v4/ver4_dict_constants.h"

namespace latinime {
namespace {

TEST(LanguageModelDictContentGlobalCountersTest, TestUpdateMaxValueOfCounters) {
    LanguageModelDictContentGlobalCounters globalCounters;

    EXPECT_FALSE(globalCounters.needsToHalveCounters());
    globalCounters.updateMaxValueOfCounters(10);
    EXPECT_FALSE(globalCounters.needsToHalveCounters());
    const int count = (1 << (Ver4DictConstants::WORD_COUNT_FIELD_SIZE * CHAR_BIT)) - 1;
    globalCounters.updateMaxValueOfCounters(count);
    EXPECT_TRUE(globalCounters.needsToHalveCounters());
    globalCounters.halveCounters();
    EXPECT_FALSE(globalCounters.needsToHalveCounters());
}

TEST(LanguageModelDictContentGlobalCountersTest, TestIncrementTotalCount) {
    LanguageModelDictContentGlobalCounters globalCounters;

    EXPECT_EQ(0, globalCounters.getTotalCount());
    globalCounters.incrementTotalCount();
    EXPECT_EQ(1, globalCounters.getTotalCount());
    for (int i = 1; i < 50; ++i) {
        globalCounters.incrementTotalCount();
    }
    EXPECT_EQ(50, globalCounters.getTotalCount());
    globalCounters.halveCounters();
    EXPECT_EQ(25, globalCounters.getTotalCount());
    globalCounters.halveCounters();
    EXPECT_EQ(12, globalCounters.getTotalCount());
    for (int i = 0; i < 4; ++i) {
        globalCounters.halveCounters();
    }
    EXPECT_EQ(0, globalCounters.getTotalCount());
}

}  // namespace
}  // namespace latinime
