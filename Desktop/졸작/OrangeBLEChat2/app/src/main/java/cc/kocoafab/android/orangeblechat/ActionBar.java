/*
    Copyright (c) 2005 nepes, kocoafab
    See the file license.txt for copying permission.
 */
package cc.kocoafab.android.orangeblechat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import cc.kocoafab.orangeblechat.R;

/**
 * 액션 바 (상단 네비게이션 레이아웃 설정)
 */
public class ActionBar extends LinearLayout {

    public ActionBar(Context context) {
        super(context);
        init();
    }

    public ActionBar(Context context, AttributeSet attrs) {
        super(context,attrs);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.actionbar, this, true);
    }
}