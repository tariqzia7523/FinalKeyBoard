#ifndef DIGRAPH_UTILS_H
#define DIGRAPH_UTILS_H

#include "defines.h"

namespace latinime {

class DictionaryHeaderStructurePolicy;

class DigraphUtils {
 public:
    typedef enum {
        NOT_A_DIGRAPH_INDEX,
        FIRST_DIGRAPH_CODEPOINT,
        SECOND_DIGRAPH_CODEPOINT
    } DigraphCodePointIndex;

    typedef enum {
        DIGRAPH_TYPE_NONE,
        DIGRAPH_TYPE_GERMAN_UMLAUT,
    } DigraphType;

    typedef struct { int first; int second; int compositeGlyph; } digraph_t;

    static bool hasDigraphForCodePoint(const DictionaryHeaderStructurePolicy *const headerPolicy,
            const int compositeGlyphCodePoint);
    static int getDigraphCodePointForIndex(const int compositeGlyphCodePoint,
            const DigraphCodePointIndex digraphCodePointIndex);

 private:
    DISALLOW_IMPLICIT_CONSTRUCTORS(DigraphUtils);
    static DigraphType getDigraphTypeForDictionary(
            const DictionaryHeaderStructurePolicy *const headerPolicy);
    static int getAllDigraphsForDigraphTypeAndReturnSize(
            const DigraphType digraphType, const digraph_t **const digraphs);
    static const digraph_t *getDigraphForCodePoint(const int compositeGlyphCodePoint);
    static const digraph_t *getDigraphForDigraphTypeAndCodePoint(
            const DigraphType digraphType, const int compositeGlyphCodePoint);

    static const digraph_t GERMAN_UMLAUT_DIGRAPHS[];
    static const DigraphType USED_DIGRAPH_TYPES[];
};
} // namespace latinime
#endif // DIGRAPH_UTILS_H
