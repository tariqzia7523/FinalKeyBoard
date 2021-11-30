#ifndef LATINIME_TYPING_SUGGEST_POLICY_FACTORY_H
#define LATINIME_TYPING_SUGGEST_POLICY_FACTORY_H

#include "defines.h"
#include "typing_suggest_policy.h"

namespace latinime {

class SuggestPolicy;

class TypingSuggestPolicyFactory {
 public:
    static const SuggestPolicy *getTypingSuggestPolicy() {
        return TypingSuggestPolicy::getInstance();
    }

 private:
    DISALLOW_COPY_AND_ASSIGN(TypingSuggestPolicyFactory);
};
} // namespace latinime
#endif // LATINIME_TYPING_SUGGEST_POLICY_FACTORY_H
