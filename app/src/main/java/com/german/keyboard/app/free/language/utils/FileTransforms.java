

package com.german.keyboard.app.free.language.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

public final class FileTransforms {
    public static OutputStream getCryptedStream(OutputStream out) {
        // Crypt the stream.
        return out;
    }

    public static InputStream getDecryptedStream(InputStream in) {
        // Decrypt the stream.
        return in;
    }

    public static InputStream getUncompressedStream(InputStream in) throws IOException {
        return new GZIPInputStream(in);
    }
}
