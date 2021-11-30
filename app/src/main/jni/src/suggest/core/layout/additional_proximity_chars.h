#ifndef LATINIME_ADDITIONAL_PROXIMITY_CHARS_H
#define LATINIME_ADDITIONAL_PROXIMITY_CHARS_H

#include <cstring>
#include <vector>

#include "defines.h"

namespace latinime {

class AdditionalProximityChars {
 private:
    DISALLOW_IMPLICIT_CONSTRUCTORS(AdditionalProximityChars);
    static const int LOCALE_EN_US_SIZE = 2;
    static const int LOCALE_EN_US[LOCALE_EN_US_SIZE];
    static const int EN_US_ADDITIONAL_A_SIZE = 4;
    static const int EN_US_ADDITIONAL_A[];
    static const int EN_US_ADDITIONAL_E_SIZE = 4;
    static const int EN_US_ADDITIONAL_E[];
    static const int EN_US_ADDITIONAL_I_SIZE = 4;
    static const int EN_US_ADDITIONAL_I[];
    static const int EN_US_ADDITIONAL_O_SIZE = 4;
    static const int EN_US_ADDITIONAL_O[];
    static const int EN_US_ADDITIONAL_U_SIZE = 4;
    static const int EN_US_ADDITIONAL_U[];

    AK_FORCE_INLINE static bool isEnLocale(const std::vector<int> *locale) {
        const int NCHARS = NELEMS(LOCALE_EN_US);
        if (locale->size() < NCHARS) {
            return false;
        }
        for (int i = 0; i < NCHARS; ++i) {
            if ((*locale)[i] != LOCALE_EN_US[i]) {
                return false;
            }
        }
        return true;
    }

 public:
    static int getAdditionalCharsSize(const std::vector<int> *locale, const int c) {
        if (!isEnLocale(locale)) {
            return 0;
        }
        switch (c) {
        case 'a':
            return EN_US_ADDITIONAL_A_SIZE;
        case 'e':
            return EN_US_ADDITIONAL_E_SIZE;
        case 'i':
            return EN_US_ADDITIONAL_I_SIZE;
        case 'o':
            return EN_US_ADDITIONAL_O_SIZE;
        case 'u':
            return EN_US_ADDITIONAL_U_SIZE;
        default:
            return 0;
        }
    }

    static const int *getAdditionalChars(const std::vector<int> *locale, const int c) {
        if (!isEnLocale(locale)) {
            return 0;
        }
        switch (c) {
        case 'a':
            return EN_US_ADDITIONAL_A;
        case 'e':
            return EN_US_ADDITIONAL_E;
        case 'i':
            return EN_US_ADDITIONAL_I;
        case 'o':
            return EN_US_ADDITIONAL_O;
        case 'u':
            return EN_US_ADDITIONAL_U;
        default:
            return 0;
        }
    }
};
} // namespace latinime
#endif // LATINIME_ADDITIONAL_PROXIMITY_CHARS_H
