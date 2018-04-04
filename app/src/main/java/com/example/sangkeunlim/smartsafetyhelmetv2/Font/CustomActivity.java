package com.example.sangkeunlim.smartsafetyhelmetv2.Font;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.tsengvn.typekit.TypekitContextWrapper;

/**
 * Created by donggun on 2018-04-02.
 */

public class CustomActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(TypekitContextWrapper.wrap(newBase));
    }
}
