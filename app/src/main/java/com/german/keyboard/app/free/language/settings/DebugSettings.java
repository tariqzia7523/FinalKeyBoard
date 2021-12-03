package com.german.keyboard.app.free.language.settings;

/**
 * Debug settings for the application.
 *
 * Note: Even though these settings are stored in the default shared preferences file,
 * they shouldn't be restored across devices.
 * If a new key is added here, it should also be blacklisted for restore in
 * {@link LocalSettingsConstants}.
 */
public final class DebugSettings {
    public static final String PREF_DEBUG_MODE = "debug_mode";
    public static final String PREF_FORCE_NON_DISTINCT_MULTITOUCH = "force_non_distinct_multitouch";
    public static final String PREF_HAS_CUSTOM_KEY_PREVIEW_ANIMATION_PARAMS =
            "pref_has_custom_key_preview_animation_params";
    public static final String PREF_RESIZE_KEYBOARD = "pref_resize_keyboard";
    public static final String PREF_KEYBOARD_HEIGHT_SCALE = "pref_keyboard_height_scale";
    public static final String PREF_KEY_PREVIEW_DISMISS_DURATION =
            "pref_key_preview_dismiss_duration";
    public static final String PREF_KEY_PREVIEW_DISMISS_END_X_SCALE =
            "pref_key_preview_dismiss_end_x_scale";
    public static final String PREF_KEY_PREVIEW_DISMISS_END_Y_SCALE =
            "pref_key_preview_dismiss_end_y_scale";
    public static final String PREF_KEY_PREVIEW_SHOW_UP_DURATION =
            "pref_key_preview_show_up_duration";
    public static final String PREF_KEY_PREVIEW_SHOW_UP_START_X_SCALE =
            "pref_key_preview_show_up_start_x_scale";
    public static final String PREF_KEY_PREVIEW_SHOW_UP_START_Y_SCALE =
            "pref_key_preview_show_up_start_y_scale";
    public static final String PREF_SHOULD_SHOW_LXX_SUGGESTION_UI =
            "pref_should_show_lxx_suggestion_ui";
    public static final String PREF_SLIDING_KEY_INPUT_PREVIEW = "pref_sliding_key_input_preview";

    private DebugSettings() {
        // This class is not publicly instantiable.
    }
}
