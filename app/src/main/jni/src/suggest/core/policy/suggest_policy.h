#ifndef LATINIME_SUGGEST_POLICY_H
#define LATINIME_SUGGEST_POLICY_H

#include "defines.h"

namespace latinime {

class Traversal;
class Scoring;
class Weighting;

class SuggestPolicy {
 public:
    SuggestPolicy() {}
    virtual ~SuggestPolicy() {}
    virtual const Traversal *getTraversal() const = 0;
    virtual const Scoring *getScoring() const = 0;
    virtual const Weighting *getWeighting() const = 0;

 private:
    DISALLOW_COPY_AND_ASSIGN(SuggestPolicy);
};
} // namespace latinime
#endif // LATINIME_SUGGEST_POLICY_H
