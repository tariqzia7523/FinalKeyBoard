#ifndef LATINIME_TYPING_SUGGEST_POLICY_H
#define LATINIME_TYPING_SUGGEST_POLICY_H

#include "defines.h"
#include "suggest/core/policy/suggest_policy.h"
#include "suggest/policyimpl/typing/typing_scoring.h"
#include "suggest/policyimpl/typing/typing_traversal.h"
#include "suggest/policyimpl/typing/typing_weighting.h"

namespace latinime {

class Scoring;
class Traversal;
class Weighting;

class TypingSuggestPolicy : public SuggestPolicy {
 public:
    static const TypingSuggestPolicy *getInstance() { return &sInstance; }

    TypingSuggestPolicy() {}
    virtual ~TypingSuggestPolicy() {}
    AK_FORCE_INLINE const Traversal *getTraversal() const {
        return TypingTraversal::getInstance();
    }

    AK_FORCE_INLINE const Scoring *getScoring() const {
        return TypingScoring::getInstance();
    }

    AK_FORCE_INLINE const Weighting *getWeighting() const {
        return TypingWeighting::getInstance();
    }

 private:
    DISALLOW_COPY_AND_ASSIGN(TypingSuggestPolicy);
    static const TypingSuggestPolicy sInstance;
};
} // namespace latinime
#endif // LATINIME_TYPING_SUGGEST_POLICY_H
