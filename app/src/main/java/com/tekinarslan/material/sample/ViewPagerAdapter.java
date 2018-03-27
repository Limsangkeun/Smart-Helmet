package com.tekinarslan.material.sample;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT =8;
    private String[] titles;

    public ViewPagerAdapter(FragmentManager fm, String[] titles2) {
        super(fm);
        titles=titles2;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            // Open FragmentTab1.java
            case 0:
                return SampleFragment.newInstance(position); //회원가입
            case 1:
                return SampleFragment1.newInstance(position); //로그인
            case 2:
                return SampleFragment2.newInstance(position); //관리자 게시판
            case 3:
                return SampleFragment3.newInstance(position); // 근로자 게시판
            case 4:
                return SampleFragment.newInstance(position);
            case 5:
                return SampleFragment.newInstance(position);
            case 6:
                return SampleFragment.newInstance(position);
            case 7:
                return SampleFragment.newInstance(position);

        }
        return null;
    }

    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}