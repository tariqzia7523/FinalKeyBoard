#ifndef LATINIME_TERMINAL_POSITION_LOOKUP_TABLE_H
#define LATINIME_TERMINAL_POSITION_LOOKUP_TABLE_H

#include <cstdio>
#include <unordered_map>

#include "defines.h"
#include "dictionary/structure/v4/content/single_dict_content.h"
#include "dictionary/structure/v4/ver4_dict_constants.h"
#include "utils/byte_array_view.h"

namespace latinime {

class TerminalPositionLookupTable : public SingleDictContent {
 public:
    typedef std::unordered_map<int, int> TerminalIdMap;

    TerminalPositionLookupTable(const ReadWriteByteArrayView buffer)
            : SingleDictContent(buffer),
              mSize(getBuffer()->getTailPosition()
                      / Ver4DictConstants::TERMINAL_ADDRESS_TABLE_ADDRESS_SIZE) {}

    TerminalPositionLookupTable() : mSize(0) {}

    int getTerminalPtNodePosition(const int terminalId) const;

    bool setTerminalPtNodePosition(const int terminalId, const int terminalPtNodePos);

    int getNextTerminalId() const {
        return mSize;
    }

    bool flushToFile(FILE *const file) const;

    bool runGCTerminalIds(TerminalIdMap *const terminalIdMap);

 private:
    DISALLOW_COPY_AND_ASSIGN(TerminalPositionLookupTable);

    int getEntryPos(const int terminalId) const {
        return terminalId * Ver4DictConstants::TERMINAL_ADDRESS_TABLE_ADDRESS_SIZE;
    }

    int mSize;
};
} // namespace latinime
#endif // LATINIME_TERMINAL_POSITION_LOOKUP_TABLE_H
