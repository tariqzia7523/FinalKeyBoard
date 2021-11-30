#include "dictionary/utils/format_utils.h"

#include "dictionary/utils/byte_array_utils.h"

namespace latinime {

const uint32_t FormatUtils::MAGIC_NUMBER = 0x9BC13AFE;

// Magic number (4 bytes), version (2 bytes), flags (2 bytes), header size (4 bytes) = 12
const size_t FormatUtils::DICTIONARY_MINIMUM_SIZE = 12;

/* static */ FormatUtils::FORMAT_VERSION FormatUtils::getFormatVersion(const int formatVersion) {
    switch (formatVersion) {
        case VERSION_2:
        case VERSION_201:
            AKLOGE("Dictionary versions 2 and 201 are incompatible with this version");
            return UNKNOWN_VERSION;
        case VERSION_202:
            return VERSION_202;
        case VERSION_4_ONLY_FOR_TESTING:
            return VERSION_4_ONLY_FOR_TESTING;
        case VERSION_402:
            return VERSION_402;
        case VERSION_403:
            return VERSION_403;
        default:
            return UNKNOWN_VERSION;
    }
}
/* static */ FormatUtils::FORMAT_VERSION FormatUtils::detectFormatVersion(
        const ReadOnlyByteArrayView dictBuffer) {
    // The magic number is stored big-endian.
    // If the dictionary is less than 4 bytes, we can't even read the magic number, so we don't
    // understand this format.
    if (dictBuffer.size() < DICTIONARY_MINIMUM_SIZE) {
        return UNKNOWN_VERSION;
    }
    const uint32_t magicNumber = ByteArrayUtils::readUint32(dictBuffer.data(), 0);
    switch (magicNumber) {
        case MAGIC_NUMBER:
            // The layout of the header is as follows:
            // Magic number (4 bytes) 0x9B 0xC1 0x3A 0xFE
            // Dictionary format version number (2 bytes)
            // Options (2 bytes)
            // Header size (4 bytes) : integer, big endian
            // Conceptually this converts the hardcoded value of the bytes in the file into
            // the symbolic value we use in the code. But we want the constants to be the
            // same so we use them for both here.
            return getFormatVersion(ByteArrayUtils::readUint16(dictBuffer.data(), 4));
        default:
            return UNKNOWN_VERSION;
    }
}

} // namespace latinime
