package com.example.Randonaut;

import android.os.Bundle;
import android.view.LayoutInflater;
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
    private  View v;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (v == null){
            v = inflater.inflate(R.layout.fragment_my_bot, container, false);
          //  Toast.makeText(getContext(), "viewisnul", Toast.LENGTH_LONG).show();
            WebView myWebView = (WebView) v.findViewById(R.id.botWebview);
            myWebView.setWebViewClient(new WebViewClient());


            WebSettings webSettings = myWebView.getSettings();

            webSettings.setJavaScriptEnabled(true);
            webSettings.setGeolocationEnabled(true);

            myWebView.setWebChromeClient(new WebChromeClient(){
                @Override
                public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {

                    callback.invoke(origin, true, false);
                }
            });

            myWebView.loadUrl("https://devbot.randonauts.com/");

        }
        getActivity().setTitle("Webbot");

        return v;
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
