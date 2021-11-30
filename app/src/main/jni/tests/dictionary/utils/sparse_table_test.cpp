#include "dictionary/utils/sparse_table.h"

#include <gtest/gtest.h>

#include "dictionary/utils/buffer_with_extendable_buffer.h"

namespace latinime {
namespace {

TEST(SparseTableTest, TestSetAndGet) {
    static const int BLOCK_SIZE = 64;
    static const int DATA_SIZE = 4;
    BufferWithExtendableBuffer indexTableBuffer(
            BufferWithExtendableBuffer::DEFAULT_MAX_ADDITIONAL_BUFFER_SIZE);
    BufferWithExtendableBuffer contentTableBuffer(
            BufferWithExtendableBuffer::DEFAULT_MAX_ADDITIONAL_BUFFER_SIZE);
    SparseTable sparseTable(&indexTableBuffer, &contentTableBuffer, BLOCK_SIZE, DATA_SIZE);

    EXPECT_FALSE(sparseTable.contains(10));
    EXPECT_TRUE(sparseTable.set(10, 100u));
    EXPECT_EQ(100u, sparseTable.get(10));
    EXPECT_TRUE(sparseTable.contains(10));
    EXPECT_TRUE(sparseTable.contains(BLOCK_SIZE - 1));
    EXPECT_FALSE(sparseTable.contains(BLOCK_SIZE));
    EXPECT_TRUE(sparseTable.set(11, 101u));
    EXPECT_EQ(100u, sparseTable.get(10));
    EXPECT_EQ(101u, sparseTable.get(11));
}

}  // namespace
}  // namespace latinime
