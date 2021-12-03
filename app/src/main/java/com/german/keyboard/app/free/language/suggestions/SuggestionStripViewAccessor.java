package com.german.keyboard.app.free.language.suggestions;

import com.german.keyboard.app.free.language.SuggestedWords;

/**
 * An object that gives basic control of a suggestion strip and some info on it.
 */
public interface SuggestionStripViewAccessor {
    void setNeutralSuggestionStrip();
    void showSuggestionStrip(final SuggestedWords suggestedWords);
}
