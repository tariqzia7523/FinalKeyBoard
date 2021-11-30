#ifndef LATINIME_BIGRAM_LIST_POLICY_H
#define LATINIME_BIGRAM_LIST_POLICY_H

#include <cstdint>

#include "defines.h"
#include "dictionary/interface/dictionary_bigrams_structure_policy.h"
#include "dictionary/structure/pt_common/bigram/bigram_list_read_write_utils.h"
#include "utils/byte_array_view.h"

namespace latinime {

class BigramListPolicy : public DictionaryBigramsStructurePolicy {
 public:
    BigramListPolicy(const ReadOnlyByteArrayView buffer) : mBuffer(buffer) {}

    ~BigramListPolicy() {}

    void getNextBigram(int *const outBigramPos, int *const outProbability, bool *const outHasNext,
            int *const pos) const {
        BigramListReadWriteUtils::BigramFlags flags;
        if (!BigramListReadWriteUtils::getBigramEntryPropertiesAndAdvancePosition(mBuffer, &flags,
                outBigramPos, pos)) {
            AKLOGE("Cannot read bigram entry. bufSize: %zd, pos: %d. ", mBuffer.size(), *pos);
            *outProbability = NOT_A_PROBABILITY;
            *outHasNext = false;
            return;
        }
        *outProbability = BigramListReadWriteUtils::getProbabilityFromFlags(flags);
        *outHasNext = BigramListReadWriteUtils::hasNext(flags);
    }

    bool skipAllBigrams(int *const pos) const {
        return BigramListReadWriteUtils::skipExistingBigrams(mBuffer, pos);
    }

 private:
    DISALLOW_IMPLICIT_CONSTRUCTORS(BigramListPolicy);

    const ReadOnlyByteArrayView mBuffer;
};
} // namespace latinime
#endif // LATINIME_BIGRAM_LIST_POLICY_H
