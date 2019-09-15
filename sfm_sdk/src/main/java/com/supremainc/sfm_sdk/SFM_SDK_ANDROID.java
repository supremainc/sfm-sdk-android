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

import com.supremainc.sfm_sdk.enumeration.UF_PROTOCOL;
import com.supremainc.sfm_sdk.enumeration.UF_RET_CODE;
import com.supremainc.sfm_sdk.enumeration.UF_UPGRADE_OPTION;
import com.supremainc.sfm_sdk.structure.UFConfigComponentHeader;
import com.supremainc.sfm_sdk.structure.UFGPIOData;
import com.supremainc.sfm_sdk.structure.UFGPIOInputData;
import com.supremainc.sfm_sdk.structure.UFGPIOOutputData;
import com.supremainc.sfm_sdk.structure.UFGPIOWiegandData;
import com.supremainc.sfm_sdk.structure.UFImage;
import com.supremainc.sfm_sdk.structure.UFLogRecord;
import com.supremainc.sfm_sdk.structure.UFOutputSignal;
import com.supremainc.sfm_sdk.structure.UFUserInfo;
import com.supremainc.sfm_sdk.structure.UFUserInfoEx;
import com.supremainc.sfm_sdk.structure.time_t;

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

    public static interface MsgCallback {
        public boolean callback(byte message);
    }

    public static interface SerialCallback {
        public void callback(final byte[] comPort, int baudrate);
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


    String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for (final byte b : a)
            sb.append(String.format("%02X ", b & 0xff));
        return sb.toString();
    }

    private ReadSerialCallback readSerialCallback = new ReadSerialCallback() {
        @Override
        public int callback(byte[] data, int size, int timeout) {

            int ret = usbService.readSerial(data, timeout);
            Log.d("[INFO] cbReadSerial", byteArrayToHex(data));
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

    public MsgCallback msgCallback = new MsgCallback() {
        @Override
        public boolean callback(byte message) {
            return false;
        }
    };

    public SerialCallback serialCallback = new SerialCallback() {
        @Override
        public void callback(byte[] comPort, int baudrate) {

        }
    };

    public UserInfoCallback userInfoCallback = new UserInfoCallback() {
        @Override
        public void callback(int index, int numOfTemplate) {

        }
    };

    public ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void callback(byte errCode) {

        }
    };

    public IdentifyCallback identifyCallback = new IdentifyCallback() {
        @Override
        public void callback(byte errCode) {

        }
    };

    public VerifyCallback verifyCallback = new VerifyCallback() {
        @Override
        public void callback(byte errCode) {

        }
    };

    public EnrollCallback enrollCallback = new EnrollCallback() {
        @Override
        public void callback(byte errCode, UF_ENROLL_MODE enrollMode, int numOfSuccess) {

        }
    };

    public DeleteCallback deleteCallback = new DeleteCallback() {
        @Override
        public void callback(byte errCode) {

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

    private void SetCommandExCallback(MsgCallback callback) {
        msgCallback = callback;
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

    /**
     * Generic command interface
     */

    public native void UF_SetProtocol(UF_PROTOCOL protocol, int moduleID);
    public native UF_PROTOCOL UF_GetProtocol();
    public native int UF_GetModuleID();
    public native void UF_SetGenericCommandTimeout(int timeout);
    public native void UF_SetInputCommandTimeout(int timeout);
    public native int UF_GetGenericCommandTimeout();
    public native int UF_GetInputCommandTimeout();
    public native void UF_SetNetWorkDelay(int delay);
    public native int UF_GetNetworkDelay();
    public native UF_RET_CODE UF_Command(byte command, int[] param, int[] size, byte[] flag);
    public native UF_RET_CODE UF_CommandEx(byte command, int[] param, int[] size, byte[] flag, MsgCallback callback);
    public native UF_RET_CODE UF_CommandSendData(byte command, int[] param, int[] size, byte[] flag, byte[] data, int dataSize);
    public native UF_RET_CODE UF_CommandSendDataEx(byte command, int[] param, int[] size, byte[] flag, byte[] data, int dataSize, MsgCallback callback, boolean waitUserInput);
    public native UF_RET_CODE UF_Cancel(boolean receivePacket);


    /**
     * Module information
     */

    public native UF_RET_CODE UF_GetModuleInfo(UF_MODULE_INFO info);

    public native String UF_GetModuleString(UF_MODULE_TYPE type, UF_MODULE_VERSION version, UF_MODULE_SENSOR sensorType);


    /**
     * Searching module
     */

    public native UF_RET_CODE UF_SearchModule(final byte[] port, int[] baudrate, boolean[] asciiMode, UF_PROTOCOL[] protocol, int[] moduleID, SerialCallback callback);

    public native UF_RET_CODE UF_SearchModuleID(int[] moduleID);

    public native UF_RET_CODE UF_SearchModuleIDEx(short[] foundModuleID, int numOfFoundID, short[] moduleID, int[] numOfID);

    public native UF_RET_CODE UF_CalibrateSensor();

    public native UF_RET_CODE UF_Reset();

    public native UF_RET_CODE UF_Lock();

    public native UF_RET_CODE UF_Unlock(final byte[] password);

    public native UF_RET_CODE UF_ChangePassword(final byte[] newPassword, final byte[] oldPassword);

    public native UF_RET_CODE UF_ReadChallengeCode(byte[] challengeCode);

    public native UF_RET_CODE UF_WriteChallengeCode(final byte[] challengeCode);

    public native UF_RET_CODE UF_PowerOff();

    /**
     * System parameters
     */
    public native void UF_InitSysParameter();
    public native UF_RET_CODE UF_GetSysParameter(UF_SYS_PARAM parameter, int[] value);

    public native UF_RET_CODE UF_SetSysParameter(UF_SYS_PARAM parameter, int value);

    public native UF_RET_CODE UF_GetMultiSysParameter(int parameterCount, UF_SYS_PARAM[] parameters, int[] values);

    public native UF_RET_CODE UF_SetMultiSysParameter(int parameterCount, UF_SYS_PARAM[] parameters, int[] values);

    public native UF_RET_CODE UF_Save();


    /**
     * Template management
     */
    public native UF_RET_CODE UF_GetNumOfTemplate(int[] numOfTemplate);

    public native UF_RET_CODE UF_GetMaxNumOfTemplate(int[] maxNumOfTemplate);

    public native UF_RET_CODE UF_GetAllUserInfo(UFUserInfo[] userInfo, int[] numOfUser, int[] numOfTemplate);

    public native UF_RET_CODE UF_GetAllUserInfoEx(UFUserInfoEx[] userInfo, int[] numOfUser, int[] numOfTemplate);

    public native void UF_SortUserInfo(UFUserInfo[] userInfo, int numOfUser);

    public native void UF_SetUserInfoCallback(UserInfoCallback callback);

    public native UF_RET_CODE UF_SetAdminLevel(int userID, UF_ADMIN_LEVEL adminLevel);

    public native UF_RET_CODE UF_GetAdminLevel(int userID, UF_ADMIN_LEVEL[] adminLevel);

    public native UF_RET_CODE UF_SetSecurityLevel(int userID, UF_USER_SECURITY_LEVEL securityLevel);

    public native UF_RET_CODE UF_GetSecurityLevel(int userID, UF_USER_SECURITY_LEVEL[] securityLevel);

    public native UF_RET_CODE UF_ClearAllAdminLevel();

    public native UF_RET_CODE UF_SaveDB(final byte[] fileName);

    public native UF_RET_CODE UF_LoadDB(final byte[] fileName);

    public native UF_RET_CODE UF_CheckTemplate(int userID, int[] numOfTemplate);

    public native UF_RET_CODE UF_ReadTemplate(int userID, int[] numOfTemplate, byte[] templateData);

    public native UF_RET_CODE UF_ReadOneTemplate(int userID, int subID, byte[] templateData);

    public native void UF_SetScanCallback(ScanCallback callback);

    public native UF_RET_CODE UF_ScanTemplate(byte[] templateData, int[] templateSize, int[] imageQuality);

    public native UF_RET_CODE UF_FixProvisionalTemplate();

    public native UF_RET_CODE UF_SetAuthType(int userID, UF_AUTH_TYPE authType);

    public native UF_RET_CODE UF_GetAuthType(int userID, UF_AUTH_TYPE[] authType);

    public native UF_RET_CODE UF_GetUserIDByAuthType(UF_AUTH_TYPE authType, int[] numOfID, int[] userID);

    public native UF_RET_CODE UF_ResetAllAuthType();

    public native UF_RET_CODE UF_AddBlacklist(int userID, int[] numOfBlacklistedID);

    public native UF_RET_CODE UF_DeleteBlacklist(int userID, int[] numOfBlacklistedID);

    public native UF_RET_CODE UF_GetBlacklist(int[] numOfBlacklistedID, int[] userID);

    public native UF_RET_CODE UF_DeleteAllBlacklist();

    public native UF_RET_CODE UF_SetEntranceLimit(int userID, int entranceLimit);

    public native UF_RET_CODE UF_GetEntranceLimit(int userID, int[] entranceLimit, int[] entranceCount);

    public native UF_RET_CODE UF_ClearAllEntranceLimit();


    /**
     * Image
     */
    public native UF_RET_CODE UF_SaveImage(final byte[] fileName, UFImage[] image);

    public native UF_RET_CODE UF_LoadImage(final byte[] fileName, UFImage[] image);

    public native UF_RET_CODE UF_ReadImage(UFImage[] image);

    public native UF_RET_CODE UF_ScanImage(UFImage[] image);


    /**
     * Identify
     */
    public native void UF_SetIdentifyCallback(IdentifyCallback callback);

    public native UF_RET_CODE UF_Identify(int[] userID, byte[] subID);

    public native UF_RET_CODE UF_IdentifyTemplate(int templateSize, byte[] templateData, int[] userID, byte[] subID);

    public native UF_RET_CODE UF_IdentifyImage(int imageSize, byte[] imageData, int[] userID, byte[] subID);

    /**
     * Verify
     */
    public native void UF_SetVerifyCallback(VerifyCallback callback);

    public native UF_RET_CODE UF_Verify(int userID, byte[] subID);

    public native UF_RET_CODE UF_VerifyTemplate(int templateSize, byte[] templateData, int userID, byte[] subID);

    public native UF_RET_CODE UF_VerifyHostTemplate(int numOfTemplate, int templateSize, byte[] templateData);

    public native UF_RET_CODE UF_VerifyImage(int imageSize, byte[] imageData, int userID, byte[] subID);

    /**
     * Enroll
     */
    public native void UF_SetEnrollCallback(EnrollCallback callback);

    public native UF_RET_CODE UF_Enroll(int userID, UF_ENROLL_OPTION option, int[] enrollID, int[] imageQuality);

    public native UF_RET_CODE UF_EnrollContinue(int userID, int[] enrollID, int[] imageQuality);

    public native UF_RET_CODE UF_EnrollAfterVerification(int userID, UF_ENROLL_OPTION option, int[] enrollID, int[] imageQuality);

    public native UF_RET_CODE UF_EnrollTemplate(int userID, UF_ENROLL_OPTION option, int templateSize, int[] templateData, int[] enrollID);

    public native UF_RET_CODE UF_EnrollMultipleTemplates(int userID, UF_ENROLL_OPTION option, int numOfTemplate, int templateSize, byte[] templateData, int[] enrollID);

    public native UF_RET_CODE UF_EnrollMultipleTemplatesEx(int userID, UF_ENROLL_OPTION option, int numOfTemplate, int numOfEnroll, int templateSize, byte[] templateData, int[] enrollID);

    public native UF_RET_CODE UF_EnrollImage(int userID, UF_ENROLL_OPTION option, int imageSize, byte[] imageData, int[] enrollID, int[] imageQuality);

    /**
     * Delete
     */
    public native void UF_SetDeleteCallback(DeleteCallback callback);

    public native UF_RET_CODE UF_Delete(int userID);

    public native UF_RET_CODE UF_DeleteOneTemplate(int userID, int subID);

    public native UF_RET_CODE UF_DeleteMultipleTemplates(int startUserID, int lastUserID, int[] deletedUserID);

    public native UF_RET_CODE UF_DeleteAll();

    public native UF_RET_CODE UF_DeleteAllAfterVerification();


    /**
     * IO for SFM3500/SFM5500
     */
    public native void UF_InitIO();

    public native UF_RET_CODE UF_SetInputFunction(UF_INPUT_PORT port, UF_INPUT_FUNC inputFunction, int minimumTime);

    public native UF_RET_CODE UF_GetInputFunction(UF_INPUT_PORT port, UF_INPUT_FUNC[] inputFunction, int[] minimumTime);

    public native UF_RET_CODE UF_GetInputStatus(UF_INPUT_PORT port, boolean remainStatus, int[] status);

    public native UF_RET_CODE UF_GetOutputEventList(UF_OUTPUT_PORT port, UF_OUTPUT_EVENT[] events, int[] numOfEvent);

    public native UF_RET_CODE UF_ClearAllOutputEvent(UF_OUTPUT_PORT port);

    public native UF_RET_CODE UF_ClearOutputEvent(UF_OUTPUT_PORT port, UF_OUTPUT_EVENT event);

    public native UF_RET_CODE UF_SetOutputEvent(UF_OUTPUT_PORT port, UF_OUTPUT_EVENT event, UFOutputSignal signal);

    public native UF_RET_CODE UF_GetOutputEvent(UF_OUTPUT_PORT port, UF_OUTPUT_EVENT event, UFOutputSignal[] signal);

    public native UF_RET_CODE UF_SetOutputStatus(UF_OUTPUT_PORT port, boolean status);

    public native UF_RET_CODE UF_SetLegacyWiegandConfig(boolean enableInput, boolean enableOutput, int fcBits, int fcCode);

    public native UF_RET_CODE UF_GetLegacyWiegandConfig(boolean[] enableInput, boolean[] enableOutput, int[] fcBits, int[] fcCode);

    public native UF_RET_CODE UF_MakeIOConfiguration(UFConfigComponentHeader[] configHeader, byte[] configData);


    /**
     * IO for SFM3000/SFM5000/SFM6000
     */
    public native UF_RET_CODE UF_GetGPIOConfiguration(UF_GPIO_PORT port, UF_GPIO_MODE[] mode, int[] numOfData, UFGPIOData[] data);

    public native UF_RET_CODE UF_SetInputGPIO(UF_GPIO_PORT port, UFGPIOInputData data);

    public native UF_RET_CODE UF_SetOutputGPIO(UF_GPIO_PORT port, int numOfData, UFGPIOOutputData[] data);

    public native UF_RET_CODE UF_SetBuzzerGPIO(UF_GPIO_PORT port, int numOfData, UFGPIOOutputData[] data);

    public native UF_RET_CODE UF_SetSharedGPIO(UF_GPIO_PORT port, UFGPIOInputData inputData, int numOfOutputData, UFGPIOOutputData[] outputData);

    public native UF_RET_CODE UF_DisableGPIO(UF_GPIO_PORT port);

    public native UF_RET_CODE UF_ClearAllGPIO();

    public native UF_RET_CODE UF_SetDefaultGPIO();

    public native UF_RET_CODE UF_EnableWiegandInput(UFGPIOWiegandData data);

    public native UF_RET_CODE UF_EnableWiegandOutput(UFGPIOWiegandData data);

    public native UF_RET_CODE UF_DisableWiegandInput();

    public native UF_RET_CODE UF_DisableWiegandOutput();

    public native UF_RET_CODE UF_MakeGPIOConfiguration(UFConfigComponentHeader[] configHeader, byte[] configData);


    /**
     * User memory
     */
    public native UF_RET_CODE UF_WriteUserMemory(byte[] memory);

    public native UF_RET_CODE UF_ReadUserMemory(byte[] memory);

    /**
     * Log and time management
     */
    // Windows only, linux, mac and android should be implemented.
    public native UF_RET_CODE UF_SetTime(time_t timeVal);

    public native UF_RET_CODE UF_GetTime(time_t[] timeVal);

    public native UF_RET_CODE UF_GetNumOfLog(int[] numOfLog, int[] numOfTotalLog);

    public native UF_RET_CODE UF_ReadLog(int startIndex, int count, UFLogRecord[] logRecord, int[] readCount);

    public native UF_RET_CODE UF_ReadLatestLog(int count, UFLogRecord[] logRecord, int[] readCount);

    public native UF_RET_CODE UF_DeleteOldestLog(int count, int[] deletedCount);

    public native UF_RET_CODE UF_DeleteAllLog();

    public native UF_RET_CODE UF_ClearLogCache();

    public native UF_RET_CODE UF_ReadLogCache(int dataPacketSize, int[] numOfLog, UFLogRecord[] logRecord);

    public native UF_RET_CODE UF_SetCustomLogField(UF_LOG_SOURCE source, int customField);

    public native UF_RET_CODE UF_GetCustomLogField(UF_LOG_SOURCE source, int[] customField);


    /**
     * Upgrade
     */
    public native UF_RET_CODE UF_Upgrade(final byte[] firmwareFilename, int dataPacketSize);

    public native UF_RET_CODE UF_UpgradeEx(final byte[] firmwareFilename, UF_UPGRADE_OPTION option, int dataPacketSize);

    public native UF_RET_CODE UF_DFU_Upgrade();

    /**
     * File System
     */
    public native UF_RET_CODE UF_FormatUserDatabase();

    public native UF_RET_CODE UF_ResetSystemConfiguration();

    /**
     * Extended Wiegand
     * Deprecated since version 3.0
     */


    /**
     * Wiegand command card
     * Deprecated since version 3.0
     */

    /**
     * Smart Card
     * Deprecated since version 3.0
     */


    /**
     * Access Control
     * Deprecated since version 3.0
     */

    /**
     * WSQ Decoding
     */
    public native UF_RET_CODE UF_WSQ_Decode(byte[][] odata, int[] ow, int[] oh, int[] od, int[] oppi, int[] lossyflag, byte[] idata, final int ilen);

    public native UF_RET_CODE UF_ReadImageEx(UFImage[] image, UF_IMAGE_TYPE type, int wsqBitRate);

    public native UF_RET_CODE UF_ScanImageEx(UFImage[] image, UF_IMAGE_TYPE type, int wsqBitRate);


    static {

        System.loadLibrary("native-lib");
    }



}
