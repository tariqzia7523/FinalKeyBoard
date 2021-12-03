

package com.german.keyboard.app.free.language.define;

/**
 * Decoder specific constants for LatinIme.
 */
public class DecoderSpecificConstants {

    // Must be equal to MAX_WORD_LENGTH in native/jni/src/defines.h
    public static final int DICTIONARY_MAX_WORD_LENGTH = 48;

    // (MAX_PREV_WORD_COUNT_FOR_N_GRAM + 1)-gram is supported in Java side. Needs to modify
    // MAX_PREV_WORD_COUNT_FOR_N_GRAM in native/jni/src/defines.h for suggestions.
    public static final int MAX_PREV_WORD_COUNT_FOR_N_GRAM = 3;

    public static final String DECODER_DICT_SUFFIX = "";

    public static final boolean SHOULD_VERIFY_MAGIC_NUMBER = true;
    public static final boolean SHOULD_VERIFY_CHECKSUM = true;
    public static final boolean SHOULD_USE_DICT_VERSION = true;
    public static final boolean SHOULD_AUTO_CORRECT_USING_NON_WHITE_LISTED_SUGGESTION = false;
    public static final boolean SHOULD_REMOVE_PREVIOUSLY_REJECTED_SUGGESTION = true;
}
