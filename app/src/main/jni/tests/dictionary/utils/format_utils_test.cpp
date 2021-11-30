#include "dictionary/utils/format_utils.h"

#include <gtest/gtest.h>

#include <vector>

#include "utils/byte_array_view.h"

namespace latinime {
namespace {

TEST(FormatUtilsTest, TestMagicNumber) {
    EXPECT_EQ(0x9BC13AFE, FormatUtils::MAGIC_NUMBER) << "Magic number must not be changed.";
}

const std::vector<uint8_t> getBuffer(const int magicNumber, const int version, const uint16_t flags,
        const size_t headerSize) {
    std::vector<uint8_t> buffer;
    buffer.push_back(magicNumber >> 24);
    buffer.push_back(magicNumber >> 16);
    buffer.push_back(magicNumber >> 8);
    buffer.push_back(magicNumber);

    buffer.push_back(version >> 8);
    buffer.push_back(version);

    buffer.push_back(flags >> 8);
    buffer.push_back(flags);

    buffer.push_back(headerSize >> 24);
    buffer.push_back(headerSize >> 16);
    buffer.push_back(headerSize >> 8);
    buffer.push_back(headerSize);
    return buffer;
}

TEST(FormatUtilsTest, TestDetectFormatVersion) {
    EXPECT_EQ(FormatUtils::UNKNOWN_VERSION,
            FormatUtils::detectFormatVersion(ReadOnlyByteArrayView()));

    {
        const std::vector<uint8_t> buffer =
                getBuffer(FormatUtils::MAGIC_NUMBER, FormatUtils::VERSION_2, 0, 0);
        EXPECT_EQ(FormatUtils::VERSION_2, FormatUtils::detectFormatVersion(
                ReadOnlyByteArrayView(buffer.data(), buffer.size())));
    }
    {
        const std::vector<uint8_t> buffer =
                getBuffer(FormatUtils::MAGIC_NUMBER, FormatUtils::VERSION_402, 0, 0);
        EXPECT_EQ(FormatUtils::VERSION_402, FormatUtils::detectFormatVersion(
                ReadOnlyByteArrayView(buffer.data(), buffer.size())));
    }
    {
        const std::vector<uint8_t> buffer =
                getBuffer(FormatUtils::MAGIC_NUMBER, FormatUtils::VERSION_403, 0, 0);
        EXPECT_EQ(FormatUtils::VERSION_403, FormatUtils::detectFormatVersion(
                ReadOnlyByteArrayView(buffer.data(), buffer.size())));
    }

    {
        const std::vector<uint8_t> buffer =
                getBuffer(FormatUtils::MAGIC_NUMBER - 1, FormatUtils::VERSION_2, 0, 0);
        EXPECT_EQ(FormatUtils::UNKNOWN_VERSION, FormatUtils::detectFormatVersion(
                ReadOnlyByteArrayView(buffer.data(), buffer.size())));
    }
    {
        const std::vector<uint8_t> buffer =
                getBuffer(FormatUtils::MAGIC_NUMBER, 100, 0, 0);
        EXPECT_EQ(FormatUtils::UNKNOWN_VERSION, FormatUtils::detectFormatVersion(
                ReadOnlyByteArrayView(buffer.data(), buffer.size())));
    }
    {
        const std::vector<uint8_t> buffer =
                getBuffer(FormatUtils::MAGIC_NUMBER, FormatUtils::VERSION_2, 0, 0);
        EXPECT_EQ(FormatUtils::UNKNOWN_VERSION, FormatUtils::detectFormatVersion(
                ReadOnlyByteArrayView(buffer.data(), buffer.size() - 1)));
    }
}

}  // namespace
}  // namespace latinime
