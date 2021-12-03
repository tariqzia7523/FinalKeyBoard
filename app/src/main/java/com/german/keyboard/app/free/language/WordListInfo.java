

package com.german.keyboard.app.free.language;

/**
 * Information container for a word list.
 */
public final class WordListInfo {
    public final String mId;
    public final String mLocale;
    public final String mRawChecksum;
    public WordListInfo(final String id, final String locale, final String rawChecksum) {
        mId = id;
        mLocale = locale;
        mRawChecksum = rawChecksum;
    }
}
