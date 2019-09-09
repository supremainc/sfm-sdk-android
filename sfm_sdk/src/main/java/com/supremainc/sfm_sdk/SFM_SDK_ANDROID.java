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
        UF_SetSetupSerialCallback(setupSerialCallback);
        UF_SetWriteSerialCallback(writeSerialCallback);
        UF_SetReadSerialCallback(readSerialCallback);


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

    /**
     * Interfaces for callback functions from Java
     */


    public static interface SetupSerialCallback{
        public void callback(int baudrate);
    }

    public static interface ReadSerialCallback {
        public int callback(byte[] data, int size, int timeout);
    }

    public static interface WriteSerialCallback{
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


    /**
     * Implementations of callback functions from Java
     */

    private SetupSerialCallback setupSerialCallback = new SetupSerialCallback() {
        @Override
        public void callback(int baudrate) {
            if (usbService != null)
                usbService.setBuadrate(baudrate);
        }
    };

    private ReadSerialCallback readSerialCallback = new ReadSerialCallback() {
        @Override
        public int callback(byte[] data, int size, int timeout) {

            int ret = usbService.readSerial(data, timeout);
            Log.d("[INFO] cbReadSerial", Arrays.toString(data));
            Log.d("[INFO]", String.format("ret : %d timeout : %d", ret, timeout));

            return ret;
        }
    };

    private WriteSerialCallback writeSerialCallback = new WriteSerialCallback() {
        @Override
        public int callback(byte[] data, int size, int timeout) {
            Log.d("[INFO] cbWriteSerial", Arrays.toString(data));
            int ret = usbService.writeSerial(data, timeout);
            return ret;
        }
    };

    private SendPacketCallback sendPacketCallback = new SendPacketCallback() {
        @Override
        public void callback(byte[] data) {
            Log.d("[INFO] cbSendPacket", Arrays.toString(data));
        }
    };

    private ReceivePacketCallback receivePacketCallback = new ReceivePacketCallback() {
        @Override
        public void callback(byte[] data) {
            Log.d("[INFO] cbReceivePacket", Arrays.toString(data));
        }
    };

    private SendDataPacketCallback sendDataPacketCallback = new SendDataPacketCallback() {
        @Override
        public void callback(int index, int numOfPacket) {

        }
    };

    private ReceiveDataPacketCallback receiveDataPacketCallback = new ReceiveDataPacketCallback() {
        @Override
        public void callback(int index, int numOfPacket) {

        }
    };

    private SendRawDataCallback sendRawDataCallback = new SendRawDataCallback() {
        @Override
        public void callback(int writtenLen, int totalSize) {

        }
    };

    private ReceiveRawDataCallback receiveRawDataCallback = new ReceiveRawDataCallback() {
        @Override
        public void callback(int readLen, int totalSize) {

        }
    };


    /**
     * Registering functions for callback functions from Java
     */

    public void UF_SetSetupSerialCallback(SetupSerialCallback callback) {
        setupSerialCallback = callback;
        UF_SetSetupSerialCallback_Android();
    }


    public void UF_SetReadSerialCallback(ReadSerialCallback callback) {
        readSerialCallback = callback;
        UF_SetReadSerialCallback_Android();
    }

    public void UF_SetWriteSerialCallback(WriteSerialCallback callback) {
        writeSerialCallback = callback;
        UF_SetWriteSerialCallback_Android();
    }

    public void UF_SetSendPacketCallback(SendPacketCallback callback) {
        sendPacketCallback = callback;
    }

    public void UF_SetReceivePacketCallback(ReceivePacketCallback callback) {
        receivePacketCallback = callback;
    }

    public void UF_SetSendDataPacketCallback(SendDataPacketCallback callback) {
        sendDataPacketCallback = callback;
    }

    public void UF_SetReceiveDataPacketCallback(ReceiveDataPacketCallback callback) {
        receiveDataPacketCallback = callback;
    }

    public void UF_SetSendRawDataCallback(SendRawDataCallback callback) {
        sendRawDataCallback = callback;
    }

    public void UF_SetReceiveRawDataCallback(ReceiveRawDataCallback callback) {
        receiveRawDataCallback = callback;
    }

    /**
     * Callback functions from JNI
     */

    public void cbSetupSerial(int baudrate)
    {
        if(setupSerialCallback != null)
            setupSerialCallback.callback(baudrate);
    }

    public int cbReadSerial(byte[] data, int timeout) throws UnsupportedEncodingException {

        int ret = 0;
        if(readSerialCallback != null)
        {
            ret = readSerialCallback.callback(data, data.length, timeout);
        }
        return ret;
    }

    public int cbWriteSerial(byte[] data, int timeout) throws UnsupportedEncodingException {

        int ret = 0;

        if(writeSerialCallback != null)
        {
            ret = writeSerialCallback.callback(data, data.length, timeout);
        }
        return ret;
    }

    public void cbSendPacket(byte[] data) {
        if (sendPacketCallback != null)
            sendPacketCallback.callback(data);
    }

    public void cbReceivePacket(byte[] data) {
        if (receivePacketCallback != null)
            receivePacketCallback.callback(data);
    }

    public void cbSendDataPacket(int index, int numOfPacket) {
        if (sendDataPacketCallback != null)
            sendDataPacketCallback.callback(index, numOfPacket);
    }

    public void cbReceiveDataPacket(int index, int numOfPacket) {
        if (receiveDataPacketCallback != null)
            receiveDataPacketCallback.callback(index, numOfPacket);
    }

    public void cbSendRawData(int writtenLen, int totalSize) {
        if (sendRawDataCallback != null)
            sendRawDataCallback.callback(writtenLen, totalSize);
    }

    public void cbReceiveRawData(int readLen, int totalSize) {
        if (receiveRawDataCallback != null)
            receiveRawDataCallback.callback(readLen, totalSize);
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

    public native void UF_SetDefaultPacketSize(int defualtSize);

    public native int UF_GetDefaultPacketSize();


    public native long UF_GetModuleID();
    public native void UF_InitSysParameter();
    public native UF_RET_CODE UF_GetSysParameter(UF_SYS_PARAM parameter, int[] value);

    static {

        System.loadLibrary("native-lib");
    }



}
