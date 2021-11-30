#ifndef LATINIME_GESTURE_SUGGEST_POLICY_FACTORY_H
#define LATINIME_GESTURE_SUGGEST_POLICY_FACTORY_H

#include "defines.h"

namespace latinime {

class SuggestPolicy;

class GestureSuggestPolicyFactory {
 public:
    static void setGestureSuggestPolicyFactoryMethod(const SuggestPolicy *(*factoryMethod)()) {
        sGestureSuggestFactoryMethod = factoryMethod;
    }

    static const SuggestPolicy *getGestureSuggestPolicy() {
        if (!sGestureSuggestFactoryMethod) {
            return 0;
        }
        return sGestureSuggestFactoryMethod();
    }

 private:
    DISALLOW_COPY_AND_ASSIGN(GestureSuggestPolicyFactory);
    static const SuggestPolicy *(*sGestureSuggestFactoryMethod)();
};
} // namespace latinime
#endif // LATINIME_GESTURE_SUGGEST_POLICY_FACTORY_H
