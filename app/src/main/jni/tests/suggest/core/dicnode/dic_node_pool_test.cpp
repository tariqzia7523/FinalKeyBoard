#include "suggest/core/dicnode/dic_node_pool.h"

#include <gtest/gtest.h>

namespace latinime {
namespace {

TEST(DicNodePoolTest, TestGet) {
    static const int CAPACITY = 10;
    DicNodePool dicNodePool(CAPACITY);

    for (int i = 0; i < CAPACITY; ++i) {
        EXPECT_NE(nullptr, dicNodePool.getInstance());
    }
    EXPECT_EQ(nullptr, dicNodePool.getInstance());
}

TEST(DicNodePoolTest, TestPlaceBack) {
    static const int CAPACITY = 1;
    DicNodePool dicNodePool(CAPACITY);

    DicNode *const dicNode = dicNodePool.getInstance();
    EXPECT_NE(nullptr, dicNode);
    EXPECT_EQ(nullptr, dicNodePool.getInstance());
    dicNodePool.placeBackInstance(dicNode);
    EXPECT_EQ(dicNode, dicNodePool.getInstance());
}

TEST(DicNodePoolTest, TestReset) {
    static const int CAPACITY_SMALL = 2;
    static const int CAPACITY_LARGE = 10;
    DicNodePool dicNodePool(CAPACITY_SMALL);

    for (int i = 0; i < CAPACITY_SMALL; ++i) {
        EXPECT_NE(nullptr, dicNodePool.getInstance());
    }
    EXPECT_EQ(nullptr, dicNodePool.getInstance());

    dicNodePool.reset(CAPACITY_LARGE);
    for (int i = 0; i < CAPACITY_LARGE; ++i) {
        EXPECT_NE(nullptr, dicNodePool.getInstance());
    }
    EXPECT_EQ(nullptr, dicNodePool.getInstance());

    dicNodePool.reset(CAPACITY_SMALL);
    for (int i = 0; i < CAPACITY_SMALL; ++i) {
        EXPECT_NE(nullptr, dicNodePool.getInstance());
    }
    EXPECT_EQ(nullptr, dicNodePool.getInstance());
}

}  // namespace
}  // namespace latinime
