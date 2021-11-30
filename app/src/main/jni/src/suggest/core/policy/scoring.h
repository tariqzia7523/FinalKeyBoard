#ifndef LATINIME_SCORING_H
#define LATINIME_SCORING_H

#include "defines.h"

namespace latinime {

class DicNode;
class DicTraverseSession;
class SuggestionResults;

// This class basically tweaks suggestions and distances apart from CompoundDistance
class Scoring {
 public:
    virtual int calculateFinalScore(const float compoundDistance, const int inputSize,
            const ErrorTypeUtils::ErrorType containedErrorTypes, const bool forceCommit,
            const bool boostExactMatches, const bool hasProbabilityZero) const = 0;
    virtual void getMostProbableString(const DicTraverseSession *const traverseSession,
            const float weightOfLangModelVsSpatialModel,
            SuggestionResults *const outSuggestionResults) const = 0;
    virtual float getAdjustedWeightOfLangModelVsSpatialModel(
            DicTraverseSession *const traverseSession, DicNode *const terminals,
            const int size) const = 0;
    virtual float getDoubleLetterDemotionDistanceCost(
            const DicNode *const terminalDicNode) const = 0;
    virtual bool autoCorrectsToMultiWordSuggestionIfTop() const = 0;
    virtual bool sameAsTyped(const DicTraverseSession *const traverseSession,
            const DicNode *const dicNode) const = 0;

 protected:
    Scoring() {}
    virtual ~Scoring() {}

 private:
    DISALLOW_COPY_AND_ASSIGN(Scoring);
};
} // namespace latinime
#endif // LATINIME_SCORING_H
