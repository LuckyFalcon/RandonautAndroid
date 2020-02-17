package com.randonautica.app;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class MyBotFragment extends Fragment {

    private  View view;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Check if view already exists
        if (view == null){
            view = inflater.inflate(R.layout.fragment_my_bot, container, false);

            //Set webView
            final WebView botWebView = (WebView) view.findViewById(R.id.botWebview);
            botWebView.setWebViewClient(new WebViewClient());

            //Set webView settings
            WebSettings webSettings = botWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setGeolocationEnabled(true);

            //Set webView client
            botWebView.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                    callback.invoke(origin, true, false);
                }
            });

            //Set webView url
            botWebView.loadUrl("https://bot.randonauts.com/");

            //Enable backpress within webview
            botWebView.setOnKeyListener(new View.OnKeyListener(){

                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK
                            && event.getAction() == MotionEvent.ACTION_UP
                            && botWebView.canGoBack()) {
                        botWebView.goBack();
                        return true;
                    }

                    return false;
                }

            });
        }

        //Set title of screen
        getActivity().setTitle("Webbot");

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


}
