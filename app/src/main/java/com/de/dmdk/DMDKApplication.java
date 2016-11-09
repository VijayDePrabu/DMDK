package com.de.dmdk;

import android.app.Application;
import android.content.ContextWrapper;

import com.de.dmdk.languagehelper.LocaleHelper;
import com.pixplicity.easyprefs.library.Prefs;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class DMDKApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
        LocaleHelper.onCreate(this, "en");

       /* CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/TAMGobi.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );*/
    }
}