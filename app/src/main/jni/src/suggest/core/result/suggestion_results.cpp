#include "suggest/core/result/suggestion_results.h"

#include "utils/jni_data_utils.h"

namespace latinime {

void SuggestionResults::outputSuggestions(JNIEnv *env, jintArray outSuggestionCount,
        jintArray outputCodePointsArray, jintArray outScoresArray, jintArray outSpaceIndicesArray,
        jintArray outTypesArray, jintArray outAutoCommitFirstWordConfidenceArray,
        jfloatArray outWeightOfLangModelVsSpatialModel) {
    int outputIndex = 0;
    while (!mSuggestedWords.empty()) {
        const SuggestedWord &suggestedWord = mSuggestedWords.top();
        suggestedWord.getCodePointCount();
        const int start = outputIndex * MAX_WORD_LENGTH;
        JniDataUtils::outputCodePoints(env, outputCodePointsArray, start,
                MAX_WORD_LENGTH /* maxLength */, suggestedWord.getCodePoint(),
                suggestedWord.getCodePointCount(), true /* needsNullTermination */);
        JniDataUtils::putIntToArray(env, outScoresArray, outputIndex, suggestedWord.getScore());
        JniDataUtils::putIntToArray(env, outSpaceIndicesArray, outputIndex,
                suggestedWord.getIndexToPartialCommit());
        JniDataUtils::putIntToArray(env, outTypesArray, outputIndex, suggestedWord.getType());
        if (mSuggestedWords.size() == 1) {
            JniDataUtils::putIntToArray(env, outAutoCommitFirstWordConfidenceArray, 0 /* index */,
                    suggestedWord.getAutoCommitFirstWordConfidence());
        }
        ++outputIndex;
        mSuggestedWords.pop();
    }
    JniDataUtils::putIntToArray(env, outSuggestionCount, 0 /* index */, outputIndex);
    JniDataUtils::putFloatToArray(env, outWeightOfLangModelVsSpatialModel, 0 /* index */,
            mWeightOfLangModelVsSpatialModel);
}

void SuggestionResults::addPrediction(const int *const codePoints, const int codePointCount,
        const int probability) {
    if (probability == NOT_A_PROBABILITY) {
        // Invalid word.
        return;
    }
    addSuggestion(codePoints, codePointCount, probability, Dictionary::KIND_PREDICTION,
            NOT_AN_INDEX, NOT_A_FIRST_WORD_CONFIDENCE);
}

void SuggestionResults::addSuggestion(const int *const codePoints, const int codePointCount,
        const int score, const int type, const int indexToPartialCommit,
        const int autocimmitFirstWordConfindence) {
    if (codePointCount <= 0 || codePointCount > MAX_WORD_LENGTH) {
        // Invalid word.
        AKLOGE("Invalid word is added to the suggestion results. codePointCount: %d",
                codePointCount);
        return;
    }
    if (getSuggestionCount() >= mMaxSuggestionCount) {
        const SuggestedWord &mWorstSuggestion = mSuggestedWords.top();
        if (score > mWorstSuggestion.getScore() || (score == mWorstSuggestion.getScore()
                && codePointCount < mWorstSuggestion.getCodePointCount())) {
            mSuggestedWords.pop();
        } else {
            return;
        }
    }
    mSuggestedWords.push(SuggestedWord(codePoints, codePointCount, score, type,
            indexToPartialCommit, autocimmitFirstWordConfindence));
}

void SuggestionResults::getSortedScores(int *const outScores) const {
    auto copyOfSuggestedWords = mSuggestedWords;
    while (!copyOfSuggestedWords.empty()) {
        const SuggestedWord &suggestedWord = copyOfSuggestedWords.top();
        outScores[copyOfSuggestedWords.size() - 1] = suggestedWord.getScore();
        copyOfSuggestedWords.pop();
    }
}

void SuggestionResults::dumpSuggestions() const {
    AKLOGE("weight of language model vs spatial model: %f", mWeightOfLangModelVsSpatialModel);
    std::vector<SuggestedWord> suggestedWords;
    auto copyOfSuggestedWords = mSuggestedWords;
    while (!copyOfSuggestedWords.empty()) {
        suggestedWords.push_back(copyOfSuggestedWords.top());
        copyOfSuggestedWords.pop();
    }
    int index = 0;
    for (auto it = suggestedWords.rbegin(); it != suggestedWords.rend(); ++it) {
        DUMP_SUGGESTION(it->getCodePoint(), it->getCodePointCount(), index, it->getScore());
        index++;
    }
}

} // namespace latinime
