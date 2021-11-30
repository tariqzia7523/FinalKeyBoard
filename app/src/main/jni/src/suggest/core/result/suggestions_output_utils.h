#ifndef LATINIME_SUGGESTIONS_OUTPUT_UTILS
#define LATINIME_SUGGESTIONS_OUTPUT_UTILS

#include "defines.h"
#include "dictionary/property/word_attributes.h"

namespace latinime {

class BinaryDictionaryShortcutIterator;
class DicNode;
class DicTraverseSession;
class Scoring;
class SuggestOptions;
class SuggestionResults;

class SuggestionsOutputUtils {
 public:
    /**
     * Returns true if we should block the incoming word, in the context of the user's
     * preferences to include or not include possibly offensive words
     */
    static bool shouldBlockWord(const SuggestOptions *const suggestOptions,
            const DicNode *const terminalDicNode, const WordAttributes wordAttributes,
            const bool isLastWord);
    /**
     * Outputs the final list of suggestions (i.e., terminal nodes).
     */
    static void outputSuggestions(const Scoring *const scoringPolicy,
            DicTraverseSession *traverseSession, const float weightOfLangModelVsSpatialModel,
            SuggestionResults *const outSuggestionResults);

 private:
    DISALLOW_IMPLICIT_CONSTRUCTORS(SuggestionsOutputUtils);

    // Inputs longer than this will autocorrect if the suggestion is multi-word
    static const int MIN_LEN_FOR_MULTI_WORD_AUTOCORRECT;

    static void outputSuggestionsOfDicNode(const Scoring *const scoringPolicy,
            DicTraverseSession *traverseSession, const DicNode *const terminalDicNode,
            const float weightOfLangModelVsSpatialModel, const bool boostExactMatches,
            const bool forceCommitMultiWords, const bool outputSecondWordFirstLetterInputIndex,
            SuggestionResults *const outSuggestionResults);
    static void outputShortcuts(BinaryDictionaryShortcutIterator *const shortcutIt,
            const int finalScore, const bool sameAsTyped,
            SuggestionResults *const outSuggestionResults);
    static int computeFirstWordConfidence(const DicNode *const terminalDicNode);
};
} // namespace latinime
#endif // LATINIME_SUGGESTIONS_OUTPUT_UTILS
