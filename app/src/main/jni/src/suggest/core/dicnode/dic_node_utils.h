#ifndef LATINIME_DIC_NODE_UTILS_H
#define LATINIME_DIC_NODE_UTILS_H

#include "defines.h"
#include "utils/int_array_view.h"

namespace latinime {

class DicNode;
class DicNodeVector;
class DictionaryStructureWithBufferPolicy;
class MultiBigramMap;

class DicNodeUtils {
 public:
    static void initAsRoot(
            const DictionaryStructureWithBufferPolicy *const dictionaryStructurePolicy,
            const WordIdArrayView prevWordIds, DicNode *const newRootDicNode);
    static void initAsRootWithPreviousWord(
            const DictionaryStructureWithBufferPolicy *const dictionaryStructurePolicy,
            const DicNode *const prevWordLastDicNode, DicNode *const newRootDicNode);
    static void initByCopy(const DicNode *const srcDicNode, DicNode *const destDicNode);
    static void getAllChildDicNodes(const DicNode *dicNode,
            const DictionaryStructureWithBufferPolicy *const dictionaryStructurePolicy,
            DicNodeVector *childDicNodes);
    static float getBigramNodeImprobability(
            const DictionaryStructureWithBufferPolicy *const dictionaryStructurePolicy,
            const DicNode *const dicNode, MultiBigramMap *const multiBigramMap);

 private:
    DISALLOW_IMPLICIT_CONSTRUCTORS(DicNodeUtils);
    // Max number of bigrams to look up
    static const int MAX_BIGRAMS_CONSIDERED_PER_CONTEXT = 500;
};
} // namespace latinime
#endif // LATINIME_DIC_NODE_UTILS_H
