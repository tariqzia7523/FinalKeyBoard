#ifndef LATINIME_BYTE_ARRAY_VIEW_H
#define LATINIME_BYTE_ARRAY_VIEW_H

#include <cstdint>
#include <cstdlib>

#include "defines.h"

namespace latinime {

/**
 * Helper class used to keep track of read accesses for a given memory region.
 */
class ReadOnlyByteArrayView {
 public:
    ReadOnlyByteArrayView() : mPtr(nullptr), mSize(0) {}

    ReadOnlyByteArrayView(const uint8_t *const ptr, const size_t size)
            : mPtr(ptr), mSize(size) {}

    AK_FORCE_INLINE size_t size() const {
        return mSize;
    }

    AK_FORCE_INLINE const uint8_t *data() const {
        return mPtr;
    }

    AK_FORCE_INLINE const ReadOnlyByteArrayView skip(const size_t n) const {
        if (mSize <= n) {
            return ReadOnlyByteArrayView();
        }
        return ReadOnlyByteArrayView(mPtr + n, mSize - n);
    }

 private:
    DISALLOW_ASSIGNMENT_OPERATOR(ReadOnlyByteArrayView);

    const uint8_t *const mPtr;
    const size_t mSize;
};

/**
 * Helper class used to keep track of read-write accesses for a given memory region.
 */
class ReadWriteByteArrayView {
 public:
    ReadWriteByteArrayView() : mPtr(nullptr), mSize(0) {}

    ReadWriteByteArrayView(uint8_t *const ptr, const size_t size)
            : mPtr(ptr), mSize(size) {}

    AK_FORCE_INLINE size_t size() const {
        return mSize;
    }

    AK_FORCE_INLINE uint8_t *data() const {
        return mPtr;
    }

    AK_FORCE_INLINE ReadOnlyByteArrayView getReadOnlyView() const {
        return ReadOnlyByteArrayView(mPtr, mSize);
    }

    ReadWriteByteArrayView subView(const size_t start, const size_t n) const {
        ASSERT(start + n <= mSize);
        return ReadWriteByteArrayView(mPtr + start, n);
    }

 private:
    // Default copy constructor and assignment operator are used for using this class with STL
    // containers.

    // These members cannot be const to have the assignment operator.
    uint8_t *mPtr;
    size_t mSize;
};

} // namespace latinime
#endif // LATINIME_BYTE_ARRAY_VIEW_H
