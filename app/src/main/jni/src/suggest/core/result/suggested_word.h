#ifndef LATINIME_SUGGESTED_WORD_H
#define LATINIME_SUGGESTED_WORD_H

#include <vector>

#include "defines.h"
#include "suggest/core/dictionary/dictionary.h"

namespace latinime {

class SuggestedWord {
 public:
    class Comparator {
     public:
        bool operator()(const SuggestedWord &left, const SuggestedWord &right) {
            if (left.getScore() != right.getScore()) {
                return left.getScore() > right.getScore();
            }
            return left.getCodePointCount() < right.getCodePointCount();
        }

     private:
        DISALLOW_ASSIGNMENT_OPERATOR(Comparator);
    };

    SuggestedWord(const int *const codePoints, const int codePointCount,
            const int score, const int type, const int indexToPartialCommit,
            const int autoCommitFirstWordConfidence)
            : mCodePoints(codePoints, codePoints + codePointCount), mScore(score),
              mType(type), mIndexToPartialCommit(indexToPartialCommit),
              mAutoCommitFirstWordConfidence(autoCommitFirstWordConfidence) {}

    const int *getCodePoint() const {
        return &mCodePoints.at(0);
    }

    int getCodePointCount() const {
        return mCodePoints.size();
    }

    int getScore() const {
        return mScore;
    }

    int getType() const {
        return mType;
    }

    int getIndexToPartialCommit() const {
        return mIndexToPartialCommit;
    }

    int getAutoCommitFirstWordConfidence() const {
        return mAutoCommitFirstWordConfidence;
    }

 private:
    DISALLOW_DEFAULT_CONSTRUCTOR(SuggestedWord);

    std::vector<int> mCodePoints;
    int mScore;
    int mType;
    int mIndexToPartialCommit;
    int mAutoCommitFirstWordConfidence;
};
} // namespace latinime
#endif /* LATINIME_SUGGESTED_WORD_H */
