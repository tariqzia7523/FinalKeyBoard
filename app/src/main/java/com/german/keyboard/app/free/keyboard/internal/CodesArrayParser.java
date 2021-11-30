package com.german.keyboard.app.free.keyboard.internal;

import android.text.TextUtils;

import com.german.keyboard.app.free.latin.common.Constants;
import com.german.keyboard.app.free.latin.common.StringUtils;

// TODO: Write unit tests for this class.
public final class CodesArrayParser {
    private static final char COMMA = Constants.CODE_COMMA;
    private static final String COMMA_REGEX = StringUtils.newSingleCodePointString(COMMA);
    private static final String VERTICAL_BAR_REGEX = // "\\|"
            new String(new char[] { Constants.CODE_BACKSLASH, Constants.CODE_VERTICAL_BAR });
    private static final int BASE_HEX = 16;

    private CodesArrayParser() {
    }

    private static String getLabelSpec(final String codesArraySpec) {
        final String[] strs = codesArraySpec.split(VERTICAL_BAR_REGEX, -1);
        if (strs.length <= 1) {
            return codesArraySpec;
        }
        return strs[0];
    }

    public static String parseLabel(final String codesArraySpec) {
        final String labelSpec = getLabelSpec(codesArraySpec);
        final StringBuilder sb = new StringBuilder();
        for (final String codeInHex : labelSpec.split(COMMA_REGEX)) {
            final int codePoint = Integer.parseInt(codeInHex, BASE_HEX);
            sb.appendCodePoint(codePoint);
        }
        return sb.toString();
    }

    private static String getCodeSpec(final String codesArraySpec) {
        final String[] strs = codesArraySpec.split(VERTICAL_BAR_REGEX, -1);
        if (strs.length <= 1) {
            return codesArraySpec;
        }
        return TextUtils.isEmpty(strs[1]) ? strs[0] : strs[1];
    }

    public static int getMinSupportSdkVersion(final String codesArraySpec) {
        final String[] strs = codesArraySpec.split(VERTICAL_BAR_REGEX, -1);
        if (strs.length <= 2) {
            return 0;
        }
        try {
            return Integer.parseInt(strs[2]);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static int parseCode(final String codesArraySpec) {
        final String codeSpec = getCodeSpec(codesArraySpec);
        if (codeSpec.indexOf(COMMA) < 0) {
            return Integer.parseInt(codeSpec, BASE_HEX);
        }
        return Constants.CODE_OUTPUT_TEXT;
    }

    public static String parseOutputText(final String codesArraySpec) {
        final String codeSpec = getCodeSpec(codesArraySpec);
        if (codeSpec.indexOf(COMMA) < 0) {
            return null;
        }
        final StringBuilder sb = new StringBuilder();
        for (final String codeInHex : codeSpec.split(COMMA_REGEX)) {
            final int codePoint = Integer.parseInt(codeInHex, BASE_HEX);
            sb.appendCodePoint(codePoint);
        }
        return sb.toString();
    }
}
