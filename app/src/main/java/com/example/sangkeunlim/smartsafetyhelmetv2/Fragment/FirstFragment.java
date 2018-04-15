package com.example.sangkeunlim.smartsafetyhelmetv2.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.example.sangkeunlim.smartsafetyhelmetv2.R;
import com.gun0912.tedpermission.util.Dlog;

/**
 * Created by donggun on 2018-04-04.
 */

public class FirstFragment extends android.support.v4.app.Fragment {
    public WebView mWebView;

    public FirstFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.fragment_first, container, false);
        FragmentActivity fa = new FragmentActivity();
        View rootView = inflater.inflate(R.layout.page, container, false);
        String userID = fa.getID();
        mWebView = (WebView) rootView.findViewById(R.id.webview);
        mWebView.loadUrl("http://wbkim11.cafe24.com/SmartHelmet/bbs.jsp?userID=" + userID);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //This is the filter
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                        Dlog.d("canGoBack");
                    } else {
                        Dlog.d("canNotGoBack");
                        ((FragmentActivity) getActivity()).onBackPressed();
                    }
                    return true;
                }
                return false;
            }
        });
        return rootView;


    }
}
