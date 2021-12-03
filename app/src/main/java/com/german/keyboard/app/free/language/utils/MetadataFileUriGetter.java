package com.german.keyboard.app.free.language.utils;

import android.content.Context;

import com.german.keyboard.app.free.R;

/**
 * Helper class to get the metadata URI and the additional ID.
 */
@SuppressWarnings("unused")
public class MetadataFileUriGetter {
    private MetadataFileUriGetter() {
        // This helper class is not instantiable.
    }

    public static String getMetadataUri(final Context context) {
        return context.getString(R.string.dictionary_pack_metadata_uri);
    }

    public static String getMetadataAdditionalId(final Context context) {
        return "";
    }
}
