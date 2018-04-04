package com.example.sangkeunlim.smartsafetyhelmetv2.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.sangkeunlim.smartsafetyhelmetv2.R;

/**
 * Created by donggun on 2018-04-04.
 */

public class FragmentActivity extends AppCompatActivity{

    ViewPager vp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        vp = (ViewPager)findViewById(R.id.vp);
        Button btn_first = (Button)findViewById(R.id.btn_first);
        Button btn_second = (Button)findViewById(R.id.btn_second);
        Button btn_third = (Button)findViewById(R.id.btn_third);

        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(0);

        btn_first.setOnClickListener(movePageListener);
        btn_first.setTag(0);
        btn_second.setOnClickListener(movePageListener);
        btn_second.setTag(1);
        btn_third.setOnClickListener(movePageListener);
        btn_third.setTag(2);
    }

    View.OnClickListener movePageListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int tag = (int)v.getTag();
            vp.setCurrentItem(tag);
        }
    };

    private class pagerAdapter extends FragmentStatePagerAdapter{

        public pagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch(position)
            {
                case 0:
                    return new FirstFragment();
                case 1:
                    return new SecondFragment();
                case 2:
                    return new ThirdFragment();
                default:
                    return null;
            }
        }


        @Override
        public int getCount() {
            return 3;
        }
    }
}
