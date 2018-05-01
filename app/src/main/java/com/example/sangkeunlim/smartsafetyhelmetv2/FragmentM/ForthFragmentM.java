package com.example.sangkeunlim.smartsafetyhelmetv2.FragmentM;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.example.sangkeunlim.smartsafetyhelmetv2.R;

/**
 * Created by donggun on 2018-04-19.
 */

public class ForthFragmentM extends android.support.v4.app.Fragment {
    WebView mWebView;
    public ForthFragmentM(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.activity_board_fragment_m, container, false);
        MFragmentActivity fa = new MFragmentActivity();
        View rootView = inflater.inflate(R.layout.page, container, false);
        String userID = fa.getID();
        mWebView = (WebView) rootView.findViewById(R.id.webview);
        mWebView.loadUrl("http://wbkim11.cafe24.com/SmartHelmet/GoogleMap.jsp");
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

      //  mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result){
                new AlertDialog.Builder(getContext()).setTitle("From Server").setMessage(message).setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                }).setCancelable(false).create().show();
                return true;
            };
        });

        return rootView;
    }
    }
