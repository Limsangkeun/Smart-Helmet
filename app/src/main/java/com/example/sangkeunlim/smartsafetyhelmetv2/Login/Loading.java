package com.example.sangkeunlim.smartsafetyhelmetv2.Login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.sangkeunlim.smartsafetyhelmetv2.R;

/**
 * Created by donggun on 2018-04-10.
 */

public class Loading extends AppCompatActivity {
    private ImageView imgHelmet;
    private Animation anim;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        initView();
        startLoading();
    }

    private void initView() {
        imgHelmet = (ImageView)findViewById(R.id.img_android);
        anim = AnimationUtils.loadAnimation(this,R.anim.loading);
        imgHelmet.setAnimation(anim);
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getBaseContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);
    }
}
