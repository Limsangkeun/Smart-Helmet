package com.example.sangkeunlim.smartsafetyhelmetv2.Font;

import android.app.Application;

import com.tsengvn.typekit.Typekit;

/**
 * Created by donggun on 2018-04-02.
 */

public class CustomFontApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Typekit.getInstance()
                .addCustom1(Typekit.createFromAsset(this,"Pacifico.ttf"))
                .addCustom2(Typekit.createFromAsset(this,"Chunkfive.otf"));
    }
}
