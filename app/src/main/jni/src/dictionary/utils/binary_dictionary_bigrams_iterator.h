#ifndef LATINIME_BINARY_DICTIONARY_BIGRAMS_ITERATOR_H
#define LATINIME_BINARY_DICTIONARY_BIGRAMS_ITERATOR_H

#include "defines.h"
#include "dictionary/interface/dictionary_bigrams_structure_policy.h"

namespace latinime {

class BinaryDictionaryBigramsIterator {
 public:
    // Empty iterator.
    BinaryDictionaryBigramsIterator()
           : mBigramsStructurePolicy(nullptr), mPos(NOT_A_DICT_POS),
             mBigramPos(NOT_A_DICT_POS), mProbability(NOT_A_PROBABILITY), mHasNext(false) {}

    BinaryDictionaryBigramsIterator(
            const DictionaryBigramsStructurePolicy *const bigramsStructurePolicy, const int pos)
            : mBigramsStructurePolicy(bigramsStructurePolicy), mPos(pos),
              mBigramPos(NOT_A_DICT_POS), mProbability(NOT_A_PROBABILITY),
              mHasNext(pos != NOT_A_DICT_POS) {}

    BinaryDictionaryBigramsIterator(BinaryDictionaryBigramsIterator &&bigramsIterator) noexcept
            : mBigramsStructurePolicy(bigramsIterator.mBigramsStructurePolicy),
              mPos(bigramsIterator.mPos), mBigramPos(bigramsIterator.mBigramPos),
              mProbability(bigramsIterator.mProbability), mHasNext(bigramsIterator.mHasNext) {}

    AK_FORCE_INLINE bool hasNext() const {
        return mHasNext;
    }

    AK_FORCE_INLINE void next() {
        mBigramsStructurePolicy->getNextBigram(&mBigramPos, &mProbability, &mHasNext, &mPos);
    }

    AK_FORCE_INLINE int getProbability() const {
        return mProbability;
    }

    AK_FORCE_INLINE int getBigramPos() const {
        return mBigramPos;
    }

 private:
    DISALLOW_COPY_AND_ASSIGN(BinaryDictionaryBigramsIterator);

    const DictionaryBigramsStructurePolicy *const mBigramsStructurePolicy;
    int mPos;
    int mBigramPos;
    int mProbability;
    bool mHasNext;
};
} // namespace latinime
#endif // LATINIME_BINARY_DICTIONARY_BIGRAMS_ITERATOR_H
