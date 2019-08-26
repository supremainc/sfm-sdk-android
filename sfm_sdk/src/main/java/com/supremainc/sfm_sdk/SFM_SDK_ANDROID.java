package com.supremainc.sfm_sdk;

import java.util.Arrays;

public class SFM_SDK_ANDROID {

    static {

        System.loadLibrary("native-lib");
    }

    /**
     * Constructor
     */
    public SFM_SDK_ANDROID(){


    }

    private static String IntArrayToString(int[] arr)
    {
        String retval = Arrays.toString(arr).replace(", ","").replace("[","").replace("]","");
        return retval;
    }

    /**
     * Destructor
     */
    public void finalize()
    {

    }


    /**
     * Overloaded functions
     * */

    public String UF_GetSDKVersion()
    {
        int[] version_major = new int[1];
        int[] version_minor = new int[1];
        int[] version_revision = new int[1];

        UF_GetSDKVersion(version_major, version_minor, version_revision);

        String retval = IntArrayToString(version_major) + "." + IntArrayToString(version_minor) + "." + IntArrayToString(version_revision);

        return retval;
    }



    /**
     * Native Functions
     */
    public native String stringFromJNI();
    public native void UF_GetSDKVersion(int[] major, int[] minor, int[] revision);
}
