#include "dictionary/structure/v4/content/probability_entry.h"

#include <gtest/gtest.h>

#include "defines.h"

namespace latinime {
namespace {

TEST(ProbabilityEntryTest, TestEncodeDecode) {
    const int flag = 0xFF;
    const int probability = 10;

    const ProbabilityEntry entry(flag, probability);
    const uint64_t encodedEntry = entry.encode(false /* hasHistoricalInfo */);
    const ProbabilityEntry decodedEntry =
            ProbabilityEntry::decode(encodedEntry, false /* hasHistoricalInfo */);
    EXPECT_EQ(0xFF0Aull, encodedEntry);
    EXPECT_EQ(flag, decodedEntry.getFlags());
    EXPECT_EQ(probability, decodedEntry.getProbability());
}

TEST(ProbabilityEntryTest, TestEncodeDecodeWithHistoricalInfo) {
    const int flag = 0xF0;
    const int timestamp = 0x3FFFFFFF;
    const int count = 0xABCD;

    const HistoricalInfo historicalInfo(timestamp, 0 /* level */, count);
    const ProbabilityEntry entry(flag, &historicalInfo);

    const uint64_t encodedEntry = entry.encode(true /* hasHistoricalInfo */);
    EXPECT_EQ(0xF03FFFFFFFABCDull, encodedEntry);
    const ProbabilityEntry decodedEntry =
            ProbabilityEntry::decode(encodedEntry, true /* hasHistoricalInfo */);

    EXPECT_EQ(flag, decodedEntry.getFlags());
    EXPECT_EQ(timestamp, decodedEntry.getHistoricalInfo()->getTimestamp());
    EXPECT_EQ(count, decodedEntry.getHistoricalInfo()->getCount());
}

}  // namespace
}  // namespace latinime
