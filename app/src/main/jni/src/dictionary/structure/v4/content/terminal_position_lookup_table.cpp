#include "dictionary/structure/v4/content/terminal_position_lookup_table.h"

#include "dictionary/utils/buffer_with_extendable_buffer.h"

namespace latinime {

int TerminalPositionLookupTable::getTerminalPtNodePosition(const int terminalId) const {
    if (terminalId < 0 || terminalId >= mSize) {
        return NOT_A_DICT_POS;
    }
    const int terminalPos = getBuffer()->readUint(
            Ver4DictConstants::TERMINAL_ADDRESS_TABLE_ADDRESS_SIZE, getEntryPos(terminalId));
    return (terminalPos == Ver4DictConstants::NOT_A_TERMINAL_ADDRESS) ?
            NOT_A_DICT_POS : terminalPos;
}

bool TerminalPositionLookupTable::setTerminalPtNodePosition(
        const int terminalId, const int terminalPtNodePos) {
    if (terminalId < 0) {
        return false;
    }
    while (terminalId >= mSize) {
        // Write new entry.
        if (!getWritableBuffer()->writeUint(Ver4DictConstants::NOT_A_TERMINAL_ADDRESS,
                Ver4DictConstants::TERMINAL_ADDRESS_TABLE_ADDRESS_SIZE, getEntryPos(mSize))) {
            return false;
        }
        mSize++;
    }
    const int terminalPos = (terminalPtNodePos != NOT_A_DICT_POS) ?
            terminalPtNodePos : Ver4DictConstants::NOT_A_TERMINAL_ADDRESS;
    return getWritableBuffer()->writeUint(terminalPos,
            Ver4DictConstants::TERMINAL_ADDRESS_TABLE_ADDRESS_SIZE, getEntryPos(terminalId));
}

bool TerminalPositionLookupTable::flushToFile(FILE *const file) const {
    // If the used buffer size is smaller than the actual buffer size, regenerate the lookup
    // table and write the new table to the file.
    if (getEntryPos(mSize) < getBuffer()->getTailPosition()) {
        TerminalPositionLookupTable lookupTableToWrite;
        for (int i = 0; i < mSize; ++i) {
            const int terminalPtNodePosition = getTerminalPtNodePosition(i);
            if (!lookupTableToWrite.setTerminalPtNodePosition(i, terminalPtNodePosition)) {
                AKLOGE("Cannot set terminal position to lookupTableToWrite."
                        " terminalId: %d, position: %d", i, terminalPtNodePosition);
                return false;
            }
        }
        return lookupTableToWrite.flush(file);
    } else {
        // We can simply use this lookup table because the buffer size has not been
        // changed.
        return flush(file);
    }
}

bool TerminalPositionLookupTable::runGCTerminalIds(TerminalIdMap *const terminalIdMap) {
    int removedEntryCount = 0;
    int nextNewTerminalId = 0;
    for (int i = 0; i < mSize; ++i) {
        const int terminalPos = getBuffer()->readUint(
                Ver4DictConstants::TERMINAL_ADDRESS_TABLE_ADDRESS_SIZE, getEntryPos(i));
        if (terminalPos == Ver4DictConstants::NOT_A_TERMINAL_ADDRESS) {
            // This entry is a garbage.
            removedEntryCount++;
        } else {
            // Give a new terminal id to the entry.
            if (!getWritableBuffer()->writeUint(terminalPos,
                    Ver4DictConstants::TERMINAL_ADDRESS_TABLE_ADDRESS_SIZE,
                    getEntryPos(nextNewTerminalId))) {
                return false;
            }
            // Memorize the mapping to the old terminal id to the new terminal id.
            terminalIdMap->insert(TerminalIdMap::value_type(i, nextNewTerminalId));
            nextNewTerminalId++;
        }
    }
    mSize = nextNewTerminalId;
    return true;
}

} // namespace latinime
