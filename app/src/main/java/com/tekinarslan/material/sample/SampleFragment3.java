package com.tekinarslan.material.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SampleFragment3 extends Fragment {

    private static final String ARG_POSITION = "position";

    private int position;
    public WebView mWebView;

    public static SampleFragment3 newInstance(int position) {
        SampleFragment3 f = new SampleFragment3();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        position = getArguments().getInt(ARG_POSITION);

        View rootView = inflater.inflate(R.layout.page, container, false);

        mWebView = (WebView) rootView.findViewById(R.id.webview);
        mWebView.loadUrl("http://192.168.100.188:8080/JSP_D_2013152014/bbs2.jsp");

        // Enable Javascript
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new WebViewClient());



        return rootView;
    }



}







