#include "dictionary/structure/v4/content/sparse_table_dict_content.h"

#include "dictionary/utils/dict_file_writing_utils.h"

namespace latinime {

const int SparseTableDictContent::LOOKUP_TABLE_BUFFER_INDEX = 0;
const int SparseTableDictContent::ADDRESS_TABLE_BUFFER_INDEX = 1;
const int SparseTableDictContent::CONTENT_BUFFER_INDEX = 2;

bool SparseTableDictContent::flush(FILE *const file) const {
    if (!DictFileWritingUtils::writeBufferToFileTail(file, &mExpandableLookupTableBuffer)) {
        return false;
    }
    if (!DictFileWritingUtils::writeBufferToFileTail(file, &mExpandableAddressTableBuffer)) {
        return false;
    }
    if (!DictFileWritingUtils::writeBufferToFileTail(file, &mExpandableContentBuffer)) {
        return false;
    }
    return true;
}

} // namespace latinime
