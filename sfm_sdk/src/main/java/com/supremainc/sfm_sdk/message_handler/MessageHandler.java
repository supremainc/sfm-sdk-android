package com.supremainc.sfm_sdk.message_handler;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class MessageHandler extends Handler {
    public final WeakReference<AppCompatActivity> mActivity;

    public MessageHandler(AppCompatActivity activity) {
        mActivity = new WeakReference<>(activity);
    }

}
