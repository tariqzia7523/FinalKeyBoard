package com.german.keyboard.app.free.language.spellcheck;

import android.service.textservice.SpellCheckerService.Session;

public abstract class AndroidSpellCheckerSessionFactory {
    public static Session newInstance(AndroidSpellCheckerService service) {
        return new AndroidSpellCheckerSession(service);
    }
}
