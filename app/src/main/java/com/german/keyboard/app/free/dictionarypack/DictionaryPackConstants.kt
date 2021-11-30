package com.german.keyboard.app.free.inputmethod.dictionarypack

object DictionaryPackConstants {
    private const val DICTIONARY_DOMAIN = "com.german.keyboard.app.free.dictionarypack.aosp"

// TODO: find some way to factorize this string with the one in the resources
    const val AUTHORITY = DICTIONARY_DOMAIN
// TODO: make this different across different packages. A suggested course of action is
    const val NEW_DICTIONARY_INTENT_ACTION = "$DICTIONARY_DOMAIN.newdict"
    const val UNKNOWN_DICTIONARY_PROVIDER_CLIENT = (DICTIONARY_DOMAIN
            + ".UNKNOWN_CLIENT")
    const val DICTIONARY_PROVIDER_CLIENT_EXTRA = "client"
    const val UPDATE_NOW_INTENT_ACTION = (DICTIONARY_DOMAIN
            + ".UPDATE_NOW")
    const val INIT_AND_UPDATE_NOW_INTENT_ACTION = (DICTIONARY_DOMAIN
            + ".INIT_AND_UPDATE_NOW")
}