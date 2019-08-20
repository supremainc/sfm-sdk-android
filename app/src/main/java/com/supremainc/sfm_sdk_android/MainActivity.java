package com.supremainc.sfm_sdk_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.supremainc.sfm_sdk.SFM_SDK_ANDROID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SFM_SDK_ANDROID sdk = new SFM_SDK_ANDROID();

        sdk.GetSDKVersion();
    }
}
