package com.suprema.www.unifingerui;

/**
 * Created by jmlee on 2015-08-28.
 */
public class Command {

    static
    {
        System.loadLibrary("Command");
    }

    public native int SetCommandClassName(String str);
    public native int SetWriteCallbackFunctionName(String str);
    public native int SetReadCallbackFunctionName(String str);
    public native int UF_Enroll(int userID);
    public native int UF_Identify();
    public native int UF_Verify(int userID);
    public native byte[] UF_ScanImage(int width, int height);
    public native void UF_SetDefaultPacketSIze(int defaultSize);
    public native byte[] UF_ReadImage(int width, int height);




}
