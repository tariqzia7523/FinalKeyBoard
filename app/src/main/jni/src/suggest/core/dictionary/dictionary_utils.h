#ifndef LATINIME_DICTIONARY_UTILS_H
#define LATINIME_DICTIONARY_UTILS_H

#include <vector>

#include "defines.h"
#include "utils/int_array_view.h"

namespace latinime {

class DictionaryStructureWithBufferPolicy;
class DicNode;

class DictionaryUtils {
 public:
    static int getMaxProbabilityOfExactMatches(
            const DictionaryStructureWithBufferPolicy *const dictionaryStructurePolicy,
            const CodePointArrayView codePoints);

 private:
    DISALLOW_IMPLICIT_CONSTRUCTORS(DictionaryUtils);

    static void processChildDicNodes(
            const DictionaryStructureWithBufferPolicy *const dictionaryStructurePolicy,
            const int inputCodePoint, const DicNode *const parentDicNode,
            std::vector<DicNode> *const outDicNodes);
};
} // namespace latinime
#endif // LATINIME_DICTIONARY_UTILS_H
