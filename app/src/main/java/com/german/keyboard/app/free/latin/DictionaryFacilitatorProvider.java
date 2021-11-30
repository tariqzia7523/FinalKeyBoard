package com.german.keyboard.app.free.latin;

/**
 * Factory for instantiating DictionaryFacilitator objects.
 */
public class DictionaryFacilitatorProvider {
    public static DictionaryFacilitator getDictionaryFacilitator(boolean isNeededForSpellChecking) {
        return new DictionaryFacilitatorImpl();
    }
}
