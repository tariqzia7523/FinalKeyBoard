package com.german.keyboard.app.free.latin.spellcheck;

import android.service.textservice.SpellCheckerService.Session;

public abstract class AndroidSpellCheckerSessionFactory {
    public static Session newInstance(AndroidSpellCheckerService service) {
        return new AndroidSpellCheckerSession(service);
    }
}
