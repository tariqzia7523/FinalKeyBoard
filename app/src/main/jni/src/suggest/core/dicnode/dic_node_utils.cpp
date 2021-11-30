#include "suggest/core/dicnode/dic_node_utils.h"

#include "dictionary/interface/dictionary_structure_with_buffer_policy.h"
#include "suggest/core/dicnode/dic_node.h"
#include "suggest/core/dicnode/dic_node_vector.h"

namespace latinime {

///////////////////////////////
// Node initialization utils //
///////////////////////////////

/* static */ void DicNodeUtils::initAsRoot(
        const DictionaryStructureWithBufferPolicy *const dictionaryStructurePolicy,
        const WordIdArrayView prevWordIds, DicNode *const newRootDicNode) {
    newRootDicNode->initAsRoot(dictionaryStructurePolicy->getRootPosition(), prevWordIds);
}

/*static */ void DicNodeUtils::initAsRootWithPreviousWord(
        const DictionaryStructureWithBufferPolicy *const dictionaryStructurePolicy,
        const DicNode *const prevWordLastDicNode, DicNode *const newRootDicNode) {
    newRootDicNode->initAsRootWithPreviousWord(
            prevWordLastDicNode, dictionaryStructurePolicy->getRootPosition());
}

/* static */ void DicNodeUtils::initByCopy(const DicNode *const srcDicNode,
        DicNode *const destDicNode) {
    destDicNode->initByCopy(srcDicNode);
}

///////////////////////////////////
// Traverse node expansion utils //
///////////////////////////////////
/* static */ void DicNodeUtils::getAllChildDicNodes(const DicNode *dicNode,
        const DictionaryStructureWithBufferPolicy *const dictionaryStructurePolicy,
        DicNodeVector *const childDicNodes) {
    if (dicNode->isTotalInputSizeExceedingLimit()) {
        return;
    }
    if (!dicNode->isLeavingNode()) {
        childDicNodes->pushPassingChild(dicNode);
    } else {
        dictionaryStructurePolicy->createAndGetAllChildDicNodes(dicNode, childDicNodes);
    }
}

///////////////////
// Scoring utils //
///////////////////
/**
 * Computes the combined bigram / unigram cost for the given dicNode.
 */
/* static */ float DicNodeUtils::getBigramNodeImprobability(
        const DictionaryStructureWithBufferPolicy *const dictionaryStructurePolicy,
        const DicNode *const dicNode, MultiBigramMap *const multiBigramMap) {
    if (dicNode->hasMultipleWords() && !dicNode->isValidMultipleWordSuggestion()) {
        return static_cast<float>(MAX_VALUE_FOR_WEIGHTING);
    }
    const WordAttributes wordAttributes = dictionaryStructurePolicy->getWordAttributesInContext(
            dicNode->getPrevWordIds(), dicNode->getWordId(), multiBigramMap);
    if (wordAttributes.getProbability() == NOT_A_PROBABILITY
            || (dicNode->hasMultipleWords()
                    && (wordAttributes.isBlacklisted() || wordAttributes.isNotAWord()))) {
        return static_cast<float>(MAX_VALUE_FOR_WEIGHTING);
    }
    // TODO: This equation to calculate the improbability looks unreasonable.  Investigate this.
    const float cost = static_cast<float>(MAX_PROBABILITY - wordAttributes.getProbability())
            / static_cast<float>(MAX_PROBABILITY);
    return cost;
}

} // namespace latinime
