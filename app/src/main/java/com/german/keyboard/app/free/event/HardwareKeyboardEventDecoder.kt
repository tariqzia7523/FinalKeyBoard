/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.german.keyboard.app.free.inputmethod.event

import android.view.KeyCharacterMap
import android.view.KeyEvent
import com.german.keyboard.app.free.latin.common.Constants

class HardwareKeyboardEventDecoder // TODO: get the layout for this hardware keyboard
(val mDeviceId: Int) : HardwareEventDecoder {
    override fun decodeHardwareKey(keyEvent: KeyEvent): Event {
        val codePointAndFlags = keyEvent.unicodeChar
        val keyCode = keyEvent.keyCode
        val isKeyRepeat = 0 != keyEvent.repeatCount
        if (KeyEvent.KEYCODE_DEL == keyCode) {
            return Event.createHardwareKeypressEvent(
                Event.NOT_A_CODE_POINT, Constants.CODE_DELETE,
                null /* next */, isKeyRepeat
            )
        }
        if (keyEvent.isPrintingKey || KeyEvent.KEYCODE_SPACE == keyCode || KeyEvent.KEYCODE_ENTER == keyCode) {
            if (0 != codePointAndFlags and KeyCharacterMap.COMBINING_ACCENT) { // A dead key.
                return Event.createDeadEvent(
                    codePointAndFlags and KeyCharacterMap.COMBINING_ACCENT_MASK, keyCode,
                    null /* next */
                )
            }
            return if (KeyEvent.KEYCODE_ENTER == keyCode) {
                if (keyEvent.isShiftPressed) {
                    Event.createHardwareKeypressEvent(
                        Event.NOT_A_CODE_POINT,
                        Constants.CODE_SHIFT_ENTER, null /* next */, isKeyRepeat
                    )
                } else Event.createHardwareKeypressEvent(
                    Constants.CODE_ENTER, keyCode,
                    null /* next */, isKeyRepeat
                )
            } else Event.createHardwareKeypressEvent(
                codePointAndFlags, keyCode, null /* next */,
                isKeyRepeat
            )
        }
        return Event.createNotHandledEvent()
    }

}