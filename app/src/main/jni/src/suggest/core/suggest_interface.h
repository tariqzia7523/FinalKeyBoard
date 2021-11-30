#ifndef LATINIME_SUGGEST_INTERFACE_H
#define LATINIME_SUGGEST_INTERFACE_H

#include "defines.h"

namespace latinime {

class ProximityInfo;
class SuggestionResults;

class SuggestInterface {
 public:
    virtual void getSuggestions(ProximityInfo *pInfo, void *traverseSession, int *inputXs,
            int *inputYs, int *times, int *pointerIds, int *inputCodePoints, int inputSize,
            const float weightOfLangModelVsSpatialModel,
            SuggestionResults *const suggestionResults) const = 0;
    SuggestInterface() {}
    virtual ~SuggestInterface() {}
 private:
    DISALLOW_COPY_AND_ASSIGN(SuggestInterface);
};
} // namespace latinime
#endif // LATINIME_SUGGEST_INTERFACE_H
