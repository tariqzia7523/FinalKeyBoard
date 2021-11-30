/*
 * Copyright (C) 2014 The Android Open Source Project
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

import com.german.keyboard.app.free.latin.settings.SettingsValues

class InputTransaction(// Initial conditions
    val mSettingsValues: SettingsValues, val mEvent: Event,
    val mTimestamp: Long, val mSpaceState: Int, val mShiftState: Int) {

    var requiredShiftUpdate = SHIFT_NO_UPDATE
        private set
    private var mRequiresUpdateSuggestions = false
    private var mDidAffectContents = false
    private var mDidAutoCorrect = false
    fun requireShiftUpdate(updateType: Int) {
        requiredShiftUpdate = Math.max(requiredShiftUpdate, updateType)
    }
    fun setRequiresUpdateSuggestions() {
        mRequiresUpdateSuggestions = true
    }

    fun requiresUpdateSuggestions(): Boolean {
        return mRequiresUpdateSuggestions
    }

    fun setDidAffectContents() {
        mDidAffectContents = true
    }

    fun didAffectContents(): Boolean {
        return mDidAffectContents
    }

    fun setDidAutoCorrect() {
        mDidAutoCorrect = true
    }

    fun didAutoCorrect(): Boolean {
        return mDidAutoCorrect
    }

    companion object {
        const val SHIFT_NO_UPDATE = 0
        const val SHIFT_UPDATE_NOW = 1
        const val SHIFT_UPDATE_LATER = 2
    }
}