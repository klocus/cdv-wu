package com.cdv.wirtualnauczelnia;


import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.webkit.JavascriptInterface;

public class CustomJSInterface {

    private Activity mActivity;
    private View mProgressbarView;

    public CustomJSInterface (Activity activity, View appView) {
        mProgressbarView = appView;
        mActivity = activity;
    }

    @JavascriptInterface
    public void removeProgressBar(){
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        removeProgressBarView();
                    }
                }, 600);
            }
        });
    }

    public void removeProgressBarView() {
        mProgressbarView.setVisibility(View.GONE);
    }
}
