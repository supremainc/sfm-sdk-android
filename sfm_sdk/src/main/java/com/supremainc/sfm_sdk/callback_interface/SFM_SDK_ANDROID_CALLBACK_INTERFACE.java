/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

package com.supremainc.sfm_sdk.callback_interface;

import com.supremainc.sfm_sdk.enumeration.UF_ENROLL_MODE;

public class SFM_SDK_ANDROID_CALLBACK_INTERFACE {
    // Interfaces for callback functions from Java (17 functions)

    public static interface SetupSerialCallback {
        public void callback(int baudrate);
    }

    public static interface ReadSerialCallback {
        public int callback(byte[] data, int size, int timeout);
    }

    public static interface WriteSerialCallback {
        public int callback(byte[] data, int size, int timeout);
    }

    public static interface SendPacketCallback {
        public void callback(byte[] data);
    }

    public static interface ReceivePacketCallback {
        public void callback(byte[] data);
    }

    public static interface SendDataPacketCallback {
        public void callback(int index, int numOfPacket);
    }

    public static interface ReceiveDataPacketCallback {
        public void callback(int index, int numOfPacket);
    }

    public static interface SendRawDataCallback {
        public void callback(int writtenLen, int totalSize);
    }

    public static interface ReceiveRawDataCallback {
        public void callback(int readLen, int totalSize);
    }

    public static interface UserInfoCallback {
        public void callback(int index, int numOfTemplate);
    }

    public static interface ScanCallback {
        public void callback(byte errCode);
    }

    public static interface IdentifyCallback {
        public void callback(byte errCode);
    }

    public static interface VerifyCallback {
        public void callback(byte errCode);
    }

    public static interface EnrollCallback {
        public void callback(byte errCode, UF_ENROLL_MODE enrollMode, int numOfSuccess);
    }

    public static interface DeleteCallback {
        public void callback(byte errCode);
    }

    public static interface MsgCallback {
        public boolean callback(byte message);
    }

    public static interface SearchModuleCallback {
        public void callback(final String comPort, int baudrate);
    }
}
