package com.german.keyboard.app.free.language.define;

import com.german.keyboard.app.free.language.SuggestedWords;

public final class ProductionFlags {
    private ProductionFlags() {
        // This class is not publicly instantiable.
    }

    public static final boolean IS_HARDWARE_KEYBOARD_SUPPORTED = false;

    /**
     * Include all suggestions from all dictionaries in
     * {@link SuggestedWords#mRawSuggestions}.
     */
    public static final boolean INCLUDE_RAW_SUGGESTIONS = false;

    /**
     * When false, the metrics logging is not yet ready to be enabled.
     */
    public static final boolean IS_METRICS_LOGGING_SUPPORTED = false;

    /**
     * When {@code false}, the split keyboard is not yet ready to be enabled.
     */
    public static final boolean IS_SPLIT_KEYBOARD_SUPPORTED = true;

}
