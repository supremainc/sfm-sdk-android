/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.debug_message;

import android.util.Log;

public class DebugMessage {

    static final String TAG = "SFM_SDK_ANDROID";
    private boolean debug_mode = false;

    public DebugMessage(boolean debug_mode) {
        this.debug_mode = debug_mode;
    }

    public void setDebugMode(boolean debug_mode) {
        this.debug_mode = debug_mode;
    }

    /** Log Level Error **/
    public void e(String message) {
        if (this.debug_mode) Log.e(this.TAG, buildLogMsg(message));
    }

    /** Log Level Warning **/
    public void w(String message) {
        if (this.debug_mode)Log.w(this.TAG, buildLogMsg(message));
    }

    /** Log Level Information **/
    public void i(String message) {
        if (this.debug_mode)Log.i(this.TAG, buildLogMsg(message));
    }

    /** Log Level Debug **/
    public void d(String message) {
        if (this.debug_mode)Log.d(this.TAG, buildLogMsg(message));
    }

    /** Log Level Verbose **/
    public void v(String message) {
        if (this.debug_mode)Log.v(this.TAG, buildLogMsg(message));
    }

    /** Log Level Error **/
    public void e(String TAG, String message) {
        if (this.debug_mode) Log.e(TAG, buildLogMsg(message));
    }

    /** Log Level Warning **/
    public void w(String TAG, String message) {
        if (this.debug_mode)Log.w(TAG, buildLogMsg(message));
    }

    /** Log Level Information **/
    public void i(String TAG, String message) {
        if (this.debug_mode)Log.i(TAG, buildLogMsg(message));
    }

    /** Log Level Debug **/
    public void d(String TAG, String message) {
        if (this.debug_mode)Log.d(TAG, buildLogMsg(message));
    }

    /** Log Level Verbose **/
    public void v(String TAG, String message) {
        if (this.debug_mode)Log.v(TAG, buildLogMsg(message));
    }


    public static String buildLogMsg(String message) {

        StackTraceElement ste = Thread.currentThread().getStackTrace()[4];

        StringBuilder sb = new StringBuilder();

        sb.append("[");
        sb.append(ste.getFileName().replace(".java", ""));
        sb.append("::");
        sb.append(ste.getMethodName());
        sb.append("]");
        sb.append(message);

        return sb.toString();

    }

}
