package com.german.keyboard.app.free.changes;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.inputmethodservice.Keyboard;
import android.view.inputmethod.EditorInfo;

import com.german.keyboard.app.free.R;

public class Keyboard_Cls extends Keyboard {
    private Key m_EnterKey;
    private Key m_ModeChangeKey;
    private Key m_LanguageSwitchKey;
    private Key m_SavedModeChangeKey;
    private Key m_SavedLanguageSwitchKey;
    public Keyboard_Cls(Context context, int xmlLayoutResId) {
        super(context, xmlLayoutResId);
    }
    public Keyboard_Cls(Context context, int layoutTemplateResId,
                        CharSequence characters, int columns, int horizontalPadding) {
        super(context, layoutTemplateResId, characters, columns, horizontalPadding);
    }
    @Override
    protected Key createKeyFromXml(Resources res, Row parent, int x, int y,
                                   XmlResourceParser parser) {
        Key key = new LatinKey(res, parent, x, y, parser);
        if (key.codes[0] == 10) {
            m_EnterKey = key;
        } else {
            if (key.codes[0] != ' ') {
                if (key.codes[0] == Keyboard.KEYCODE_MODE_CHANGE) {
                    m_ModeChangeKey = key;
                    m_SavedModeChangeKey = new LatinKey(res, parent, x, y, parser);
                } else if (key.codes[0] == Keyboard_View_Cls.KEYCODE_LANGUAGE_SWITCH) {
                    m_LanguageSwitchKey = key;
                    m_SavedLanguageSwitchKey = new LatinKey(res, parent, x, y, parser);
                }
            }
        }
        return key;
    }
    public void setLanguageSwitchKeyVisibility(boolean visible) {
        if (visible) {

            try {
                m_ModeChangeKey.width = m_SavedModeChangeKey.width;
                m_ModeChangeKey.x = m_SavedModeChangeKey.x;
                m_LanguageSwitchKey.width = m_SavedLanguageSwitchKey.width;
                m_LanguageSwitchKey.icon = m_SavedLanguageSwitchKey.icon;
                m_LanguageSwitchKey.iconPreview = m_SavedLanguageSwitchKey.iconPreview;
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else {
            m_ModeChangeKey.width = m_SavedModeChangeKey.width + m_SavedLanguageSwitchKey.width;
            m_LanguageSwitchKey.width = 0;
            m_LanguageSwitchKey.icon = null;
            m_LanguageSwitchKey.iconPreview = null;
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public void setImeOptions(Resources res, int options) {
        if (m_EnterKey == null) {
            return;
        }
        switch (options & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
            case EditorInfo.IME_ACTION_GO:
                m_EnterKey.iconPreview = null;
                m_EnterKey.icon = null;
                m_EnterKey.label = res.getText(R.string.label_go_key);
                break;
            case EditorInfo.IME_ACTION_NEXT:
                m_EnterKey.iconPreview = null;
                m_EnterKey.icon = null;
                m_EnterKey.label = res.getText(R.string.custom_label_next_key);
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                m_EnterKey.icon = res.getDrawable(R.drawable.ic_search);
                m_EnterKey.label = null;
                break;
            case EditorInfo.IME_ACTION_SEND:
                m_EnterKey.iconPreview = null;
                m_EnterKey.icon = null;
                m_EnterKey.label = res.getText(R.string.label_send_key);
                break;
            default:
                m_EnterKey.icon = res.getDrawable(R.drawable.ic_return);
                m_EnterKey.label = null;
                break;
        }
    }
    private static class LatinKey extends Key {

        LatinKey(Resources res, Row parent, int x, int y,
                 XmlResourceParser parser) {
            super(res, parent, x, y, parser);
        }
        @Override
        public boolean isInside(int x, int y) {
            return super.isInside(x, codes[0] == KEYCODE_CANCEL ? y - 10 : y);
        }
    }


}