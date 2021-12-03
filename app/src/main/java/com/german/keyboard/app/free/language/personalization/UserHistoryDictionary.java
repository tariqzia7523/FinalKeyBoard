package com.german.keyboard.app.free.language.personalization;

import android.content.Context;

import com.german.keyboard.app.free.inputmethod.annotations.ExternallyReferenced;
import com.german.keyboard.app.free.inputmethod.annotations.UsedForTesting;
import com.german.keyboard.app.free.language.BinaryDictionary;
import com.german.keyboard.app.free.language.Dictionary;
import com.german.keyboard.app.free.language.ExpandableBinaryDictionary;
import com.german.keyboard.app.free.language.NgramContext;
import com.german.keyboard.app.free.language.makedict.DictionaryHeader;

import java.io.File;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Locally gathers statistics about the words user types and various other signals like
 * auto-correction cancellation or manual picks. This allows the keyboard to adapt to the
 * typist over time.
 */
public class UserHistoryDictionary extends ExpandableBinaryDictionary {
    static final String NAME = UserHistoryDictionary.class.getSimpleName();

    // TODO: Make this constructor private
    UserHistoryDictionary(final Context context, final Locale locale,
            @Nullable final String account) {
        super(context, getUserHistoryDictName(NAME, locale, null /* dictFile */, account), locale, Dictionary.TYPE_USER_HISTORY, null);
        if (mLocale != null && mLocale.toString().length() > 1) {
            reloadDictionaryIfRequired();
        }
    }

    /**
     * @returns the name of the {@link UserHistoryDictionary}.
     */
    @UsedForTesting
    static String getUserHistoryDictName(final String name, final Locale locale,
            @Nullable final File dictFile, @Nullable final String account) {
        return getDictName(name, locale, dictFile);
    }

    // Note: This method is called by {@link DictionaryFacilitator} using Java reflection.
    @SuppressWarnings("unused")
    @ExternallyReferenced
    public static UserHistoryDictionary getDictionary(final Context context, final Locale locale,
            final File dictFile, final String dictNamePrefix, @Nullable final String account) {
        return PersonalizationHelper.getUserHistoryDictionary(context, locale, account);
    }

    /**
     * Add a word to the user history dictionary.
     *
     * @param userHistoryDictionary the user history dictionary
     * @param ngramContext the n-gram context
     * @param word the word the user inputted
     * @param isValid whether the word is valid or not
     * @param timestamp the timestamp when the word has been inputted
     */
    public static void addToDictionary(final ExpandableBinaryDictionary userHistoryDictionary,
            @Nonnull final NgramContext ngramContext, final String word, final boolean isValid,
            final int timestamp) {
        if (word.length() > BinaryDictionary.DICTIONARY_MAX_WORD_LENGTH) {
            return;
        }
        userHistoryDictionary.updateEntriesForWord(ngramContext, word,
                isValid, 1 /* count */, timestamp);
    }

    @Override
    public void close() {
        // Flush pending writes.
        asyncFlushBinaryDictionary();
        super.close();
    }

    @Override
    protected Map<String, String> getHeaderAttributeMap() {
        final Map<String, String> attributeMap = super.getHeaderAttributeMap();
        attributeMap.put(DictionaryHeader.USES_FORGETTING_CURVE_KEY,
                DictionaryHeader.ATTRIBUTE_VALUE_TRUE);
        attributeMap.put(DictionaryHeader.HAS_HISTORICAL_INFO_KEY,
                DictionaryHeader.ATTRIBUTE_VALUE_TRUE);
        return attributeMap;
    }

    @Override
    protected void loadInitialContentsLocked() {
        // No initial contents.
    }

    @Override
    public boolean isValidWord(final String word) {
        // Strings out of this dictionary should not be considered existing words.
        return false;
    }
}
