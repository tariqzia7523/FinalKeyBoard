/*
 * !!!!! DO NOT EDIT THIS FILE !!!!!
 *
 * This file was generated from
 *   dictionary/structure/v4/content/terminal_position_lookup_table.h
 */

#ifndef LATINIME_BACKWARD_V402_TERMINAL_POSITION_LOOKUP_TABLE_H
#define LATINIME_BACKWARD_V402_TERMINAL_POSITION_LOOKUP_TABLE_H

#include <unordered_map>

#include "defines.h"
#include "dictionary/structure/backward/v402/content/single_dict_content.h"
#include "dictionary/structure/backward/v402/ver4_dict_constants.h"

namespace latinime {
namespace backward {
namespace v402 {

class TerminalPositionLookupTable : public SingleDictContent {
 public:
    typedef std::unordered_map<int, int> TerminalIdMap;

    TerminalPositionLookupTable(const char *const dictPath, const bool isUpdatable)
            : SingleDictContent(dictPath,
                      Ver4DictConstants::TERMINAL_ADDRESS_TABLE_FILE_EXTENSION, isUpdatable),
              mSize(getBuffer()->getTailPosition()
                      / Ver4DictConstants::TERMINAL_ADDRESS_TABLE_ADDRESS_SIZE) {}

    TerminalPositionLookupTable() : mSize(0) {}

    int getTerminalPtNodePosition(const int terminalId) const;

    bool setTerminalPtNodePosition(const int terminalId, const int terminalPtNodePos);

    int getNextTerminalId() const {
        return mSize;
    }

    bool flushToFile(const char *const dictPath) const;

    bool runGCTerminalIds(TerminalIdMap *const terminalIdMap);

 private:
    DISALLOW_COPY_AND_ASSIGN(TerminalPositionLookupTable);

    int getEntryPos(const int terminalId) const {
        return terminalId * Ver4DictConstants::TERMINAL_ADDRESS_TABLE_ADDRESS_SIZE;
    }

    int mSize;
};
} // namespace v402
} // namespace backward
} // namespace latinime
#endif // LATINIME_BACKWARD_V402_TERMINAL_POSITION_LOOKUP_TABLE_H
