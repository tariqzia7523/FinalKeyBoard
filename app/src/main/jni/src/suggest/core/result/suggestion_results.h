#ifndef LATINIME_SUGGESTION_RESULTS_H
#define LATINIME_SUGGESTION_RESULTS_H

#include <queue>
#include <vector>

#include "defines.h"
#include "jni.h"
#include "suggest/core/result/suggested_word.h"

namespace latinime {

class SuggestionResults {
 public:
    explicit SuggestionResults(const int maxSuggestionCount)
            : mMaxSuggestionCount(maxSuggestionCount),
              mWeightOfLangModelVsSpatialModel(NOT_A_WEIGHT_OF_LANG_MODEL_VS_SPATIAL_MODEL),
              mSuggestedWords() {}

    // Returns suggestion count.
    void outputSuggestions(JNIEnv *env, jintArray outSuggestionCount, jintArray outCodePointsArray,
            jintArray outScoresArray, jintArray outSpaceIndicesArray, jintArray outTypesArray,
            jintArray outAutoCommitFirstWordConfidenceArray,
            jfloatArray outWeightOfLangModelVsSpatialModel);
    void addPrediction(const int *const codePoints, const int codePointCount, const int score);
    void addSuggestion(const int *const codePoints, const int codePointCount,
            const int score, const int type, const int indexToPartialCommit,
            const int autocimmitFirstWordConfindence);
    void getSortedScores(int *const outScores) const;
    void dumpSuggestions() const;

    void setWeightOfLangModelVsSpatialModel(const float weightOfLangModelVsSpatialModel) {
        mWeightOfLangModelVsSpatialModel = weightOfLangModelVsSpatialModel;
    }

    int getSuggestionCount() const {
        return mSuggestedWords.size();
    }

 private:
    DISALLOW_IMPLICIT_CONSTRUCTORS(SuggestionResults);

    const int mMaxSuggestionCount;
    float mWeightOfLangModelVsSpatialModel;
    std::priority_queue<
            SuggestedWord, std::vector<SuggestedWord>, SuggestedWord::Comparator> mSuggestedWords;
};
} // namespace latinime
#endif // LATINIME_SUGGESTION_RESULTS_H
