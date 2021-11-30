#include "gesture_suggest_policy_factory.h"

namespace latinime {
    const SuggestPolicy *(*GestureSuggestPolicyFactory::sGestureSuggestFactoryMethod)() = 0;
} // namespace latinime
