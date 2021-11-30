#ifndef LATINIME_EDIT_DISTANCE_POLICY_H
#define LATINIME_EDIT_DISTANCE_POLICY_H

#include "defines.h"

namespace latinime {

class EditDistancePolicy {
 public:
    virtual float getSubstitutionCost(const int index0, const int index1) const = 0;
    virtual float getDeletionCost(const int index0, const int index1) const = 0;
    virtual float getInsertionCost(const int index0, const int index1) const = 0;
    virtual bool allowTransposition(const int index0, const int index1) const = 0;
    virtual float getTranspositionCost(const int index0, const int index1) const = 0;
    virtual int getString0Length() const = 0;
    virtual int getString1Length() const = 0;

 protected:
    EditDistancePolicy() {}
    virtual ~EditDistancePolicy() {}

 private:
    DISALLOW_COPY_AND_ASSIGN(EditDistancePolicy);
};
} // namespace latinime

#endif  // LATINIME_EDIT_DISTANCE_POLICY_H
