

package com.german.keyboard.app.free.language.utils;

import com.german.keyboard.app.free.language.AssetFileAddress;
import com.german.keyboard.app.free.language.makedict.DictionaryHeader;

import java.io.File;

public class DictionaryHeaderUtils {

    public static int getContentVersion(AssetFileAddress fileAddress) {
        final DictionaryHeader header = DictionaryInfoUtils.getDictionaryFileHeaderOrNull(
                new File(fileAddress.mFilename), fileAddress.mOffset, fileAddress.mLength);
        return Integer.parseInt(header.mVersionString);
    }
}
