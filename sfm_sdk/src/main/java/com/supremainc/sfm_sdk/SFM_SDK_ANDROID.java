package com.supremainc.sfm_sdk;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.supremainc.sfm_sdk.enumeration.UF_RET_CODE;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Set;

public class SFM_SDK_ANDROID {


    private UsbService usbService = null;
    private MessageHandler mHandler = null;
    private static AppCompatActivity mActivity = null;
    private static BroadcastReceiver mUsbReceiver = null;


    /**
     * Constructor
     */

    public SFM_SDK_ANDROID(AppCompatActivity activity)
    {
        mActivity = activity;
    }
    public SFM_SDK_ANDROID(AppCompatActivity activity, MessageHandler handler)
    {
        mActivity = activity;
        mHandler = handler;
    }
    public SFM_SDK_ANDROID(AppCompatActivity activity, MessageHandler handler, BroadcastReceiver receiver){
        mActivity = activity;
        mHandler = handler;
        mUsbReceiver = receiver;
    }
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            if(mHandler != null)
                usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(mActivity, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            mActivity.startService(startService);
        }
        Intent bindingIntent = new Intent(mActivity, service);
        mActivity.bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        if(mUsbReceiver != null)
            mActivity.registerReceiver(mUsbReceiver, filter);
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

    public UF_RET_CODE UF_InitCommPort(int baudrate, boolean asciiMode) {
        String commPort = null;
        if (usbService != null)
            commPort = usbService.getUsbDeviceName();


        // set callback funcgtion.
        UF_SetSetupSerialCallback_Android();
        UF_SetReadSerialCallback_Android();
        UF_SetWriteSerialCallback_Android();


        UF_RET_CODE ret = UF_InitCommPort(commPort, baudrate, asciiMode);

        return ret;
    }


    public void WriteTest(String data)
    {
        if (usbService != null) { // if UsbService was correctly binded, Send data
            byte[] a = data.getBytes();
            Log.d("[INFO write]", Arrays.toString(a));
            usbService.writeSerial(data.getBytes(), 1000);

            byte[] readpacket = new byte[4];
            usbService.readSerial(readpacket, 1000);
            Log.d("[INFO read]", Arrays.toString(readpacket));

        }
    }

    public void resumeService()
    {
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it

    }

    public void pauseService()
    {
        if(mUsbReceiver !=null)
            mActivity.unregisterReceiver(mUsbReceiver);
        mActivity.unbindService(usbConnection);
    }

    public void cbSetupSerial(int baudrate)
    {
        if(usbService != null)
            usbService.setBuadrate(baudrate);
    }

    public int cbReadSerial(byte[] data, int timeout) throws UnsupportedEncodingException {

        int ret = usbService.readSerial(data, timeout);
        Log.d("[INFO] cbReadSerial", Arrays.toString(data));
        Log.d("[INFO]", String.format("ret : %d timeout : %d", ret, timeout));

        return ret;
    }

    public int cbWriteSerial(byte[] data, int timeout) throws UnsupportedEncodingException {
        Log.d("[INFO] cbWriteSerial", Arrays.toString(data));
        int ret = usbService.writeSerial(data, timeout);
        return ret;
    }

    /**
     * Native Functions
     */
    public native String stringFromJNI();
    public native void UF_GetSDKVersion(int[] major, int[] minor, int[] revision);

    /**
     * Initialize serial communication
     */
    public native UF_RET_CODE UF_InitCommPort(String commPort, int baudrate, boolean asciiMode);
    public native UF_RET_CODE UF_CloseCommPort();

    // Deprecated
    // public native UF_RET_CODE UF_InitSocket(String inetAddr, int port, boolean asciiMode);
    // public native UF_RET_CODE UF_CloseSocket();

    /**
     *  Set callback funtions for android
     */
    public native void UF_SetSetupSerialCallback_Android();
    public native void UF_SetReadSerialCallback_Android();
    public native void UF_SetWriteSerialCallback_Android();


    public native void UF_Reconnect(); // OK
    public native UF_RET_CODE UF_SetBaudrate(int baudrate);
    public native void UF_SetAsciiMode(boolean asciiMode);

    /**
     * Basic packet interface
     */
    public native UF_RET_CODE UF_SendPacket(byte command, int param, int size, byte flag, int timeout);
    public native UF_RET_CODE UF_SendNetworkPacket(byte command, short terminalID, int param, int size, byte flag, int timeout);
    public native UF_RET_CODE UF_ReceivePakcet(byte[] packet, int timeout);
    public native UF_RET_CODE UF_ReceiveNetworkPakcet(byte[] packet, int timeout);
    public native UF_RET_CODE UF_SendRawData(byte[] buf, int size, int timeout);
    public native UF_RET_CODE UF_ReceiveRawData(byte[] buf, int size, int timeout, boolean checkEndCode);
    public native UF_RET_CODE UF_SendDataPacket(byte command, byte[] buf, int dataSize, int dataPacketSize);
    public native UF_RET_CODE UF_ReceiveDataPacket(byte command, byte[] buf, int dataSize);

    public native long UF_GetModuleID();
    public native void UF_InitSysParameter();
    public native UF_RET_CODE UF_GetSysParameter(UF_SYS_PARAM parameter, int[] value);

    static {

        System.loadLibrary("native-lib");
    }

}
