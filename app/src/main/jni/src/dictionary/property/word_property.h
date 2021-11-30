#ifndef LATINIME_WORD_PROPERTY_H
#define LATINIME_WORD_PROPERTY_H

#include <vector>

#include "defines.h"
#include "dictionary/property/ngram_property.h"
#include "dictionary/property/unigram_property.h"
#include "utils/int_array_view.h"

namespace latinime {

// This class is used for returning information belonging to a word to java side.
class WordProperty {
 public:
    // Default constructor is used to create an instance that indicates an invalid word.
    WordProperty()
            : mCodePoints(), mUnigramProperty(), mNgrams() {}

    WordProperty(const std::vector<int> &&codePoints, const UnigramProperty &unigramProperty,
            const std::vector<NgramProperty> &ngrams)
            : mCodePoints(std::move(codePoints)), mUnigramProperty(unigramProperty),
              mNgrams(ngrams) {}

    const CodePointArrayView getCodePoints() const {
        return CodePointArrayView(mCodePoints);
    }

    const UnigramProperty &getUnigramProperty() const {
        return mUnigramProperty;
    }

    const std::vector<NgramProperty> &getNgramProperties() const {
        return mNgrams;
    }

 private:
    // Default copy constructor is used for using as a return value.
    DISALLOW_ASSIGNMENT_OPERATOR(WordProperty);

    const std::vector<int> mCodePoints;
    const UnigramProperty mUnigramProperty;
    const std::vector<NgramProperty> mNgrams;
};
} // namespace latinime
#endif // LATINIME_WORD_PROPERTY_H
