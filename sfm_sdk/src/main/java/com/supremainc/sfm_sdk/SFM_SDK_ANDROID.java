/*
 * Copyright (c) 2001 - 2019. Suprema Inc. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for details.
 */

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

import com.supremainc.sfm_sdk.callback_interface.SFM_SDK_ANDROID_CALLBACK_INTERFACE;
import com.supremainc.sfm_sdk.enumeration.UF_ADMIN_LEVEL;
import com.supremainc.sfm_sdk.enumeration.UF_AUTH_TYPE;
import com.supremainc.sfm_sdk.enumeration.UF_ENROLL_MODE;
import com.supremainc.sfm_sdk.enumeration.UF_ENROLL_OPTION;
import com.supremainc.sfm_sdk.enumeration.UF_IMAGE_TYPE;
import com.supremainc.sfm_sdk.enumeration.UF_MODULE_SENSOR;
import com.supremainc.sfm_sdk.enumeration.UF_MODULE_TYPE;
import com.supremainc.sfm_sdk.enumeration.UF_MODULE_VERSION;
import com.supremainc.sfm_sdk.enumeration.UF_PROTOCOL;
import com.supremainc.sfm_sdk.enumeration.UF_RET_CODE;
import com.supremainc.sfm_sdk.enumeration.UF_SYS_PARAM;
import com.supremainc.sfm_sdk.enumeration.UF_USER_SECURITY_LEVEL;
import com.supremainc.sfm_sdk.message_handler.MessageHandler;
import com.supremainc.sfm_sdk.structure.UFImage;
import com.supremainc.sfm_sdk.structure.UFModuleInfo;
import com.supremainc.sfm_sdk.structure.UFUserInfo;
import com.supremainc.sfm_sdk.structure.UFUserInfoEx;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Set;

/**
 * Class of the SFM SDK for Android.
 */
public class SFM_SDK_ANDROID extends SFM_SDK_ANDROID_CALLBACK_INTERFACE {


    private UsbService usbService = null;
    private MessageHandler mHandler = null;
    private static AppCompatActivity mActivity = null;
    private static BroadcastReceiver mUsbReceiver = null;

    /**
     * Constructor
     */

    /**
     * Constructor of the SFM SDK for Android.
     *
     * @param activity An instance of main activity. This is must be passed by the constructor.
     */
    public SFM_SDK_ANDROID(AppCompatActivity activity) {
        mActivity = activity;
    }

    /**
     * Constructor of the SFM SDK for Android.
     *
     * @param activity An instance of main activity. This is must be passed by the constructor.
     * @param handler  A handler which can bring the message of the SDK to the main activity.
     */
    public SFM_SDK_ANDROID(AppCompatActivity activity, MessageHandler handler) {
        mActivity = activity;
        mHandler = handler;
    }

    /**
     * Constructor of the SFM SDK for Android.
     *
     * @param activity An instance of main activity. This is must be passed by the constructor.
     * @param handler  A handler which can bring the message of the SDK to the main activity.
     * @param receiver A receiver which can bring the broadcasting message such as
     *                 ACTION_USB_PERMISSION_GRANTED, ACTION_USB_DISCONNECTED and  etc. from the
     *                 UsbSerial library.
     */
    public SFM_SDK_ANDROID(AppCompatActivity activity, MessageHandler handler, BroadcastReceiver receiver) {
        mActivity = activity;
        mHandler = handler;
        mUsbReceiver = receiver;
    }

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            if (mHandler != null)
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
        if (mUsbReceiver != null)
            mActivity.registerReceiver(mUsbReceiver, filter);
    }


    private static String IntArrayToString(int[] arr) {
        String retval = Arrays.toString(arr).replace(", ", "").replace("[", "").replace("]", "");
        return retval;
    }

    /**
     * Destructor
     */
    public void finalize() {

    }

    /**
     * Overloaded functions
     */

    /**
     * Get SDK Version
     *
     * @return A string of SDK version. (eg. 2.3.0)
     */
    public String UF_GetSDKVersion() {
        int[] version_major = new int[1];
        int[] version_minor = new int[1];
        int[] version_revision = new int[1];

        UF_GetSDKVersion(version_major, version_minor, version_revision);

        String retval = IntArrayToString(version_major) + "." + IntArrayToString(version_minor) + "." + IntArrayToString(version_revision);

        return retval;
    }

    /**
     * Opens a serial port and configures communication parameters. This function should be
     * called  first before calling any other APIs.
     *
     * @param baudrate  Specifies the baud rate at which the serial port operates. Available baud
     *                  rates are 9600, 19200, 38400, 57600, 115200bps, 230400bps,460800bps,
     *                  921600bps are available. The default setting of SFM modules is 115200bps.
     * @param asciiMode Determines the packet translation mode. If it is set to TRUE, the binary
     *                  packet is converted to ASCII format first before being sent to the module.
     *                  Response packets are in ASCII format, too. The default setting of SFM
     *                  modules  is binary mode.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */

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

    /**
     * Get Device Name. ( Serial Port Name )
     *
     * @return A string of device name.
     */
    public String UF_GetDeviceName() {
        String deviceName = null;
        if (usbService != null)
            deviceName = usbService.getUsbDeviceName();

        return deviceName;
    }


    /**
     * Resume Usb Service.
     */
    public void resumeService() {
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it

    }

    /**
     * Pause Usb Service.
     */
    public void pauseService() {
        if (mUsbReceiver != null)
            mActivity.unregisterReceiver(mUsbReceiver);
        mActivity.unbindService(usbConnection);
    }


    //region Utility functions

    /**
     * Get a string from the byte array.
     *
     * @param a byte array
     * @return A string that changed from the byte array.
     */
    public String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for (final byte b : a)
            sb.append(String.format("%02X ", b & 0xff));
        return sb.toString();
    }
    //endregion


    //region Implementations of callback functions from Java (16 functions )

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
//            Log.d("[INFO] cbReadSerial", byteArrayToHex(data));
//            Log.d("[INFO]", String.format("ret : %d timeout : %d", ret, timeout));

            return ret;
        }
    };

    private WriteSerialCallback writeSerialCallback = new WriteSerialCallback() {
        @Override
        public int callback(byte[] data, int size, int timeout) {
//            Log.d("[INFO] cbWriteSerial", Arrays.toString(data));
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

    private UserInfoCallback userInfoCallback = new UserInfoCallback() {
        @Override
        public void callback(int index, int numOfTemplate) {

        }
    };

    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void callback(byte errCode) {

        }
    };

    private IdentifyCallback identifyCallback = new IdentifyCallback() {
        @Override
        public void callback(byte errCode) {

        }
    };

    private VerifyCallback verifyCallback = new VerifyCallback() {
        @Override
        public void callback(byte errCode) {

        }
    };

    private EnrollCallback enrollCallback = new EnrollCallback() {
        @Override
        public void callback(byte errCode, UF_ENROLL_MODE enrollMode, int numOfSuccess) {

        }
    };

    private DeleteCallback deleteCallback = new DeleteCallback() {
        @Override
        public void callback(byte errCode) {

        }
    };


    // This callback is passed by the native function parameter
    private MsgCallback msgCallback = new MsgCallback() {
        @Override
        public boolean callback(byte message) {
            return false;
        }
    };

    // This callback is passed by the native function parameter
    private SearchModuleCallback searchModuleCallback = new SearchModuleCallback() {
        @Override
        public void callback(String comPort, int baudrate) {

        }
    };

    //endregion


    //region Registering functions for callback functions from Java (15 functions to set callback)

    /**
     * Sets the callback function of changing baudrate.
     *
     * @param callback The callback function.
     */
    public void UF_SetSetupSerialCallback(SetupSerialCallback callback) {
        setupSerialCallback = callback;
    }

    /**
     * Sets the callback function of reading the serial data from the android system.
     *
     * @param callback The callback function.
     */
    public void UF_SetReadSerialCallback(ReadSerialCallback callback) {
        readSerialCallback = callback;
    }

    /**
     * Sets the callback function of writing the serial data from the android system.
     *
     * @param callback The callback function.
     */
    public void UF_SetWriteSerialCallback(WriteSerialCallback callback) {
        writeSerialCallback = callback;
    }

    /**
     * Sets the callback function of sending packets.
     *
     * @param callback The callback function.
     */
    public void UF_SetSendPacketCallback(SendPacketCallback callback) {
        sendPacketCallback = callback;
    }

    /**
     * Sets the callback function of receiving packets.
     *
     * @param callback The callback function.
     */
    public void UF_SetReceivePacketCallback(ReceivePacketCallback callback) {
        receivePacketCallback = callback;
    }

    /**
     * Sets the callback function of sending data packets.
     *
     * @param callback The callback function.
     */
    public void UF_SetSendDataPacketCallback(SendDataPacketCallback callback) {
        sendDataPacketCallback = callback;
    }

    /**
     * Sets the callback function of receiving data packets.
     *
     * @param callback The callback function.
     */
    public void UF_SetReceiveDataPacketCallback(ReceiveDataPacketCallback callback) {
        receiveDataPacketCallback = callback;
    }

    /**
     * Sets the callback function of sending raw data.
     *
     * @param callback The callback function.
     */
    public void UF_SetSendRawDataCallback(SendRawDataCallback callback) {
        sendRawDataCallback = callback;
    }

    /**
     * sets the callback function of receiving raw data.
     *
     * @param callback The callback function.
     */
    public void UF_SetReceiveRawDataCallback(ReceiveRawDataCallback callback) {
        receiveRawDataCallback = callback;
    }

    /**
     * sets the callback function for getting user information.
     *
     * @param callback The callback function.
     */
    public void UF_SetUserInfoCallback(UserInfoCallback callback) {
        userInfoCallback = callback;
    }

    /**
     * sets the callback function for scanning fingerprints
     *
     * @param callback The callback function.
     */
    public void UF_SetScanCallback(ScanCallback callback) {
        scanCallback = callback;
    }

    /**
     * Sets the callback function for identification.
     *
     * @param callback The callback function.
     */
    public void UF_SetIdentifyCallback(IdentifyCallback callback) {
        identifyCallback = callback;
    }

    /**
     * Sets the callback function for verification process.
     *
     * @param callback The callback function.
     */
    public void UF_SetVerifyCallback(VerifyCallback callback) {
        verifyCallback = callback;
    }

    /**
     * Sets the callback function for enrollment process.
     *
     * @param callback The callback function.
     */
    public void UF_SetEnrollCallback(EnrollCallback callback) {
        enrollCallback = callback;
    }

    /**
     * Sets the callback function for delete process.
     *
     * @param callback The callback function.
     */
    public void UF_SetDeleteCallback(DeleteCallback callback) {
        deleteCallback = callback;
    }
    //endregion


    //region Callback functions calling from JNI
    private void cbSetupSerial(int baudrate) {
        if (setupSerialCallback != null)
            setupSerialCallback.callback(baudrate);
    }

    private int cbReadSerial(byte[] data, int timeout) throws UnsupportedEncodingException {

        int ret = 0;
        if (readSerialCallback != null) {
            ret = readSerialCallback.callback(data, data.length, timeout);
        }
        return ret;
    }

    private int cbWriteSerial(byte[] data, int timeout) throws UnsupportedEncodingException {

        int ret = 0;

        if (writeSerialCallback != null) {
            ret = writeSerialCallback.callback(data, data.length, timeout);
        }
        return ret;
    }

    private void cbSendPacket(byte[] data) {
        if (sendPacketCallback != null)
            sendPacketCallback.callback(data);
    }

    private void cbReceivePacket(byte[] data) {
        if (receivePacketCallback != null)
            receivePacketCallback.callback(data);
    }

    private void cbSendDataPacket(int index, int numOfPacket) {
        if (sendDataPacketCallback != null)
            sendDataPacketCallback.callback(index, numOfPacket);
    }

    private void cbReceiveDataPacket(int index, int numOfPacket) {
        if (receiveDataPacketCallback != null)
            receiveDataPacketCallback.callback(index, numOfPacket);
    }

    private void cbSendRawData(int writtenLen, int totalSize) {
        if (sendRawDataCallback != null)
            sendRawDataCallback.callback(writtenLen, totalSize);
    }

    private void cbReceiveRawData(int readLen, int totalSize) {
        if (receiveRawDataCallback != null)
            receiveRawDataCallback.callback(readLen, totalSize);
    }

    private void cbUserInfo(int index, int numOfTemplate) {
        if (userInfoCallback != null)
            userInfoCallback.callback(index, numOfTemplate);
    }

    private void cbScan(byte errCode) {
        if (scanCallback != null)
            scanCallback.callback(errCode);
    }

    private void cbIdentify(byte errCode) {
        if (identifyCallback != null)
            identifyCallback.callback(errCode);
    }

    private void cbVerify(byte errCode) {
        if (verifyCallback != null)
            verifyCallback.callback(errCode);
    }

    private void cbEnroll(byte errCode, UF_ENROLL_MODE enrollMode, int numOfSuccess) {
        if (enrollCallback != null)
            enrollCallback.callback(errCode, enrollMode, numOfSuccess);
    }

    private void cbDelete(byte errCode) {
        if (deleteCallback != null)
            deleteCallback.callback(errCode);
    }
    //endregion


    /**
     * Native Functions
     */
    public native String stringFromJNI();


    /**
     * Get SDK version
     *
     * @param major    major digit
     * @param minor    minor digit
     * @param revision revision digit
     */

    /**
     * Get SDK Version
     *
     * @param major    major digit
     * @param minor    minor digit
     * @param revision revision digit
     */
    public native void UF_GetSDKVersion(int[] major, int[] minor, int[] revision);


    /**
     * Initialize serial communication
     */

    /**
     * Opens a serial port and configures communication parameters. This function should be
     * called first before calling any other APIs.
     *
     * @param commPort  A string that specifies the name of the serial port.
     * @param baudrate  Specifies the baud rate at which the serial port operates. Available baud
     *                  rates are 9600, 19200, 38400, 57600, 115200bps, 230400bps,460800bps,
     *                  921600bps are available. The default setting of SFM modules is 115200bps.
     * @param asciiMode Determines the packet translation mode. If it is set to TRUE, the binary
     *                  packet is converted to ASCII format first before being sent to the module.
     *                  Response packets are in ASCII format, too. The default setting of SFM
     *                  modules  is binary mode.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_InitCommPort(String commPort, int baudrate, boolean asciiMode);


    /**
     * Closes the serial port opened by UF_InitCommPort.
     *
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding  error code.
     */
    public native UF_RET_CODE UF_CloseCommPort();

    // UNSUPPORTED
    // public native UF_RET_CODE UF_InitSocket(String inetAddr, int port, boolean asciiMode);
    // public native UF_RET_CODE UF_CloseSocket();

    /**
     * To improve communication efficiency, the SDK caches basic information of a module such as
     * system parameters and I/O settings. UF_Reconnect clears this cached information. When
     * changing  the modules connected to the serial port, this function should be called.
     */
    public native void UF_Reconnect();


    /**
     * Changes the baud rate.
     *
     * @param baudrate Specifies the baud rate at which the serial port operates. Available baud
     *                 rates are 9600, 19200, 38400, 57600, 115200bps, 230400bps, 460800bps,
     *                 921600bps. The default setting of SFM modules is 115200bps.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding  error code.
     * @see com.supremainc.sfm_sdk.callback_interface.SFM_SDK_ANDROID_CALLBACK_INTERFACE.SetupSerialCallback
     */
    public native UF_RET_CODE UF_SetBaudrate(int baudrate);

    /**
     * Changes the packet translation mode.
     *
     * @param asciiMode TRUE for ascii format, FALSE for binary format.
     */
    public native void UF_SetAsciiMode(boolean asciiMode);

    /**
     * Basic packet interface
     */
    /**
     * Sends a 13 byte packet to the module.
     *
     * @param command Command field of a packet. Refer to the Packet Protocol Manual for
     *                available  commands.
     * @param param   Param field of a packet.
     * @param size    Size field of a packet.
     * @param flag    Flag field of a packet.
     * @param timeout Sets the timeout in milliseconds. If sending does not complete within this
     *                limit, UF_ERR_WRITE_SERIAL_TIMEOUT will be returned.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding  error code.
     */
    public native UF_RET_CODE UF_SendPacket(byte command, int param, int size, byte flag, int timeout);

    /**
     * Sends a 15 byte network packet to the specified module. In order to support RS422 or RS485
     * network interfaces, SFM modules support Network Packet Protocol. Network packet is
     * composed  of 15 bytes, whose start code is different from the standard packet, and
     * includes  2 bytes for terminal ID. The terminal ID is equal to the lower 2 bytes of Module
     * ID of system parameter.
     *
     * @param command    Command field of a packet. Refer to the Packet Protocol Manual for
     *                   available  commands.
     * @param terminalID Specifies the terminal ID of the receiving module.
     * @param param      Param field of a packet.
     * @param size       Size field of a packet.
     * @param flag       Flag field of a packet.
     * @param timeout    Sets the timeout in milliseconds. If sending does not complete within this
     *                   limit, UF_ERR_WRITE_SERIAL_TIMEOUT will be returned.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding  error code.
     */
    public native UF_RET_CODE UF_SendNetworkPacket(byte command, short terminalID, int param, int size, byte flag, int timeout);

    /**
     * Receives a 13 byte packet from the module. Most commands of Packet Protocol can be
     * implemented  by a pair of {@link #UF_SendPacket(byte, int, int, byte, int)}/
     * {@link  #UF_ReceivePakcet(byte[], int)} or
     * {@link #UF_SendNetworkPacket(byte, short, int, int, byte, int)}/
     * {@link #UF_ReceiveNetworkPakcet(byte[], int)}
     *
     * @param packet  The 13 byte packet.
     * @param timeout Sets the timeout in milliseconds. If receiving does not complete within
     *                this limit, UF_ERR_READ_SERIAL_TIMEOUT will be returned.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_ReceivePakcet(byte[] packet, int timeout);

    /**
     * Receives a 15 byte network packet from the specified module.
     *
     * @param packet  The 15 byte packet.
     * @param timeout Sets the timeout in milliseconds. If receiving does not complete within
     *                this limit, UF_ERR_READ_SERIAL_TIMEOUT will be returned.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_ReceiveNetworkPakcet(byte[] packet, int timeout);

    /**
     * Some commands such as ET(Enroll Template) and IT(Identify Template) send additional data
     * after the 13/15 byte request packet. {@link #UF_SendRawData(byte[], int, int)} is used in
     * these cases for sending the data.
     *
     * @param buf     Data buffer.
     * @param size    Number of bytes to be sent.
     * @param timeout Sets the timeout in milliseconds. If sending does not complete within this
     *                limit, UF_ERR_WRITE_SERIAL_TIMEOUT will be returned.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_SendRawData(byte[] buf, int size, int timeout);

    /**
     * Some commands such as ST(Scan Template) and RT(Read Template) return additional data after
     * the 13/15 byte response packet. {@link #UF_ReceiveRawData(byte[], int, int, boolean)} is
     * used in these cases for receiving the data.
     *
     * @param buf          Data buffer.
     * @param size         Number of bytes to be received.
     * @param timeout      Sets the timeout in milliseconds. If receiving does not complete within this
     *                     limit, UF_ERR_READ_SERIAL_TIMEOUT will be returned.
     * @param checkEndCode Data transfer ends with ‘0x0a’. If this parameter is FALSE, the
     *                     function returns without checking the end code.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_ReceiveRawData(byte[] buf, int size, int timeout, boolean checkEndCode);

    /**
     * Sends data using Extended Data Transfer Protocol. Dividing large data into small blocks
     * can reduce communication errors between the host and the module. Extended Data Transfer
     * Protocol is an extension of Packet Protocol to provide a reliable and customizable
     * communication for large data. In Extended Data Transfer Protocol, data is divided into
     * multiple data packets. And a data packet consists of fixed-length header, variable-length
     * data body, and 4 byte checksum. Commands which use the Extended Data Transfer Protocols
     * are EIX, VIX, IIX, RIX, SIX, and UG.
     *
     * @param command        Command field of a packet. Valid commands are EIX, VIX, IIX, RIX, SIX, and
     *                       UG.
     * @param buf            Data buffer
     * @param dataSize       Number of bytes to be sent.
     * @param dataPacketSize Size of data packet. For example, if dataSize is 16384 bytes and
     *                       dataPacketSize is 4096 bytes, the data will be divided into 4 data
     *                       packets.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_SendDataPacket(byte command, byte[] buf, int dataSize, int dataPacketSize);

    /**
     * Receives data using Extended Data Transfer Protocol. The size of data packet should be
     * specified before calling this function.
     *
     * @param command  Command field of a packet. Valid commands are EIX, VIX, IIX, RIX, SIX, and UG.
     * @param buf      Data Buffer
     * @param dataSize Number of bytes to be received.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_ReceiveDataPacket(byte command, byte[] buf, int dataSize);

    /**
     * Sets the size of data packets used in Extended Data Transfer protocol. The default value
     * is 4096. When BEACon is used as an Ethernet-to-Serial converter, this value should not be
     * larger than 256.
     *
     * @param defualtSize Size of data packet.
     */
    public native void UF_SetDefaultPacketSize(int defualtSize);

    /**
     * Returns the size of data packet used in Extended Data Transfer protocol.
     *
     * @return The size of data packet.
     */
    public native int UF_GetDefaultPacketSize();

    /**
     * Generic command interface
     */

    /**
     * Selects packet protocol. If the host connects to the single module through RS232
     * interface, use UF_SINGLE_PROTOCOL. If there are multiple modules in RS422/485 networks,
     * use UF_NETWORK_PROTOCOL. The protocol should also be compatible with the Network Mode
     * system parameter.
     *
     * @param protocol UF_SINGLE_PROTOCOL for 13 byte packet protocol, UF_NETWORK_PROTOCOL for 15
     *                 byte network packet protocol.
     * @param moduleID Specifies the ID of the module. This parameter is applicable when the
     *                 protocol is set to UF_NETWORK_PROTOCOL.
     */
    public native void UF_SetProtocol(UF_PROTOCOL protocol, int moduleID);

    /**
     * Gets the selected protocol.
     *
     * @return UF_SINGLE_PROTOCOL or UF_NETWORK_PROTOCOL.
     */
    public native UF_PROTOCOL UF_GetProtocol();

    /**
     * Gets the ID of the module.
     *
     * @return ID of the module.
     */
    public native int UF_GetModuleID();

    /**
     * Sets the timeout for generic commands. The default timeout is 2,000ms.
     *
     * @param timeout Specifies the timeout period in milliseconds.
     */
    public native void UF_SetGenericCommandTimeout(int timeout);

    /**
     * Sets the timeout for commands which need user input. The default timeout is 10,000ms.
     *
     * @param timeout Specifies the timeout period in milliseconds.
     */
    public native void UF_SetInputCommandTimeout(int timeout);

    /**
     * Gets the timeout for generic commands.
     *
     * @return Timeout for generic commands.
     */
    public native int UF_GetGenericCommandTimeout();

    /**
     * Gets the timeout for commands which need user input.
     *
     * @return Timeout for commands which need user input.
     */
    public native int UF_GetInputCommandTimeout();

    /**
     * In half duplex mode, the same communication lines are shared for sending and receiving
     * data. To prevent packet collisions on the shared line, there should be some delay between
     * receiving and sending data. The default delay is set to 40ms. This value can be optimized
     * for specific environments.
     *
     * @param delay Specified the delay in milliseconds.
     */
    public native void UF_SetNetWorkDelay(int delay);

    /**
     * Gets the network delay.
     *
     * @return Delay in milliseconds.
     */
    public native int UF_GetNetworkDelay();

    /**
     * Encapsulates the commands composed of one request packet and one response packet. The
     * majority of commands can be implemented using UF_Command.
     *
     * @param command Command field of a packet. Refer to the Packet Protocol Manual for available commands.
     * @param param   Param field of a packet. This parameter is used both for input and output.
     * @param size    Size field of a packet. This parameter is used both for input and output.
     * @param flag    Flag field of a packet. This parameter is used both for input and output.
     * @return If packets are transferred successfully, return UF_RET_SUCCESS. Otherwise, return
     * the corresponding error code. UF_RET_SUCCESS only means that request packet is received
     * successfully. To know if the operation succeeds, the flag field should be checked.
     */
    public native UF_RET_CODE UF_Command(byte command, int[] param, int[] size, byte[] flag);

    /**
     * Encapsulates the commands composed of one request packet and multiple response packets.
     * Command such as ES(Enroll) and IS(Identify) can have more than one response packet. To
     * handle these cases, UF_CommandEx requires a message callback function, which should return
     * TRUE when the received packet is the last one.
     *
     * @param command  Command field of a packet. Refer to the Packet Protocol Manual for available commands.
     * @param param    Param field of a packet. This parameter is used both for input and output.
     * @param size     Size field of a packet. This parameter is used both for input and output.
     * @param flag     Flag field of a packet. This parameter is used both for input and output.
     * @param callback The callback function. This callback is called when a response packet is
     *                 received. If the callback return TRUE, UF_CommandEx will return
     *                 immediately. If the callback return FALSE, UF_CommandEx will wait for
     *                 another response packet.
     * @return If packets are transferred successfully, return UF_RET_SUCCESS. Otherwise, return
     * the corresponding error code. UF_RET_SUCCESS only means that request packet is received
     * successfully. To know if the operation succeeds, the flag field should be checked.
     */
    public native UF_RET_CODE UF_CommandEx(byte command, int[] param, int[] size, byte[] flag, MsgCallback callback);

    /**
     * Encapsulates the commands which send additional data after a request packet. For example,
     * GW(Write GPIO Configuration) command should send configuration data after the request
     * packet.
     *
     * @param command  Command field of a packet. Refer to the Packet Protocol Manual for
     *                 available commands.
     * @param param    Param field of a packet. This parameter is used both for input and output.
     * @param size     Size field of a packet. This parameter is used both for input and output.
     * @param flag     Flag field of a packet. This parameter is used both for input and output.
     * @param data     The data buffer to be sent.
     * @param dataSize Number of bytes to be sent.
     * @return If packets are transferred successfully, return UF_RET_SUCCESS. Otherwise, return
     * the corresponding error code. UF_RET_SUCCESS only means that request packet is received
     * successfully. To know if the operation succeeds, the flag field should be checked.
     */
    public native UF_RET_CODE UF_CommandSendData(byte command, int[] param, int[] size, byte[] flag, byte[] data, int dataSize);

    /**
     * Encapsulates the commands which send additional data and have multiple response packets.
     * For example, ET(Enroll Template) command sends template data after request packet and can
     * have multiple response packets.
     *
     * @param command       Command field of a packet. Refer to the Packet Protocol Manual for
     *                      available commands.
     * @param param         Param field of a packet. This parameter is used both for input and output.
     * @param size          Size field of a packet. This parameter is used both for input and output.
     * @param flag          Flag field of a packet. This parameter is used both for input and output.
     * @param data          The data buffer to be sent.
     * @param dataSize      Number of bytes to be sent.
     * @param callback      The callback function. This callback is called when a response
     *                      packet is received. If the callback return TRUE, UF_CommandSendDataEx will
     *                      return immediately. If the callback return FALSE, UF_CommandSendDataEx
     *                      will wait for another response packet.
     * @param waitUserInput TRUE if the command needs user input. Otherwise, FALSE.
     * @return If packets are transferred successfully, return UF_RET_SUCCESS. Otherwise, return
     * the corresponding error code. UF_RET_SUCCESS only means that request packet is received
     * successfully. To know if the operation succeeds, the flag field should be checked.
     */
    public native UF_RET_CODE UF_CommandSendDataEx(byte command, int[] param, int[] size, byte[] flag, byte[] data, int dataSize, MsgCallback callback, boolean waitUserInput);

    /**
     * Cancels the command which is being processed by the module. When the module is executing a
     * command which needs user input to proceed, the status of the module will be changed to
     * UF_SYS_BUSY. If users want to execute another command before finishing the current one,
     * they can explicitly cancel it by this function.
     *
     * @param receivePacket If TRUE, UF_Cancel waits until the response packet is received. If
     *                      FALSE, UF_Cancel returns immediately after sending the request packet.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_Cancel(boolean receivePacket);


    /**
     * Module information
     */

    /**
     * Retrieves the type, version and sensor information of the module.
     *
     * @param info Object of the UFModuleInfo ({@link UFModuleInfo} including type, version snd
     *             sensor type of the module.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_GetModuleInfo(UFModuleInfo info);

    /**
     * Retrieves a string that describes the module information. This function should be called
     * after UF_GetModuleInfo.
     *
     * @param type       Specifies the type of the module.
     * @param version    Specifies the version number of the module.
     * @param sensorType Specifies the sensor type of the module.
     * @return Formatted string of the module information.
     */
    public native String UF_GetModuleString(UF_MODULE_TYPE type, UF_MODULE_VERSION version, UF_MODULE_SENSOR sensorType);


    /**
     * Searching module
     */

    /**
     * Search a module connected to the specified serial port. UF_SearchModule tries all
     * combinations of communication parameters. If it finds any module on the serial port, it
     * returns the communication parameters and its module ID.
     *
     * @param port      Serial port.
     * @param baudrate  The baud rate to be returned.
     * @param asciiMode The packet translation mode to be returned.
     * @param protocol  The protocol type to be returned.
     * @param moduleID  The module ID to be returned.
     * @param callback  The callback function. The callback function can be used for displaying
     *                  the progress of the search. This parameter can be NULL.
     * @return If it finds a module, return UF_RET_SUCCESS. If the search fails, return
     * UF_ERR_NOT_FOUND. Otherwise, return the corresponding error code.
     */
    public native UF_RET_CODE UF_SearchModule(final String port, int[] baudrate, boolean[] asciiMode, UF_PROTOCOL[] protocol, int[] moduleID, SearchModuleCallback callback);

    /**
     * UF_SerachModuleID can be used to retrieve the ID of the module in these cases. Refer to ID
     * command section in the Packet Protocol Manual for details.
     *
     * @param moduleID The module ID to be returned.
     * @return If it finds a module, return UF_RET_SUCCESS. If the search fails, return
     * UF_ERR_NOT_FOUND. Otherwise, return the corresponding error code.
     */
    public native UF_RET_CODE UF_SearchModuleID(int[] moduleID);

    /**
     * UF_SearchModuleID is used for searching a module. To search multiple modules in a
     * RS422/485 network, UF_SearchModuleIDEx should be used instead. By calling this function
     * repetitively, users can search all the modules connected to a network.
     *
     * @param foundModuleID the array of module IDs, which are already found. When the ID of a
     *                      module is in this array, the module will ignore the search command.
     * @param numOfFoundID  Number of module IDs, which are already found.
     * @param moduleID      The array of module IDs, which will be filled with newly found IDs.
     * @param numOfID       The number of module IDs to be returned.
     * @return If it finds one or more modules, return UF_RET_SUCCESS. If the search fails,
     * return UF_ERR_NOT_FOUND. Otherwise, return the corresponding error code.
     */
    public native UF_RET_CODE UF_SearchModuleIDEx(short[] foundModuleID, int numOfFoundID, short[] moduleID, int[] numOfID);


    /**
     * Calibrate sensor , Reset module
     */

    /**
     * Calibrates fingerprint sensor. This function is supported for AuthenTec’s FingerLoc AF-S2
     * and UPEK’s TouchChip. After using the UF_CalibrateSensor, UF_Save should be called to save
     * calibration data into flash memory.
     *
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_CalibrateSensor();

    /**
     * Resets the module.
     *
     * @return UF_RET_SUCCESS
     */
    public native UF_RET_CODE UF_Reset();

    /**
     * Lock/Unlock Module
     */

    /**
     * Locks the module. When the module is locked, it returns UF_ERR_LOCKED to functions other
     * than UF_Unlock.
     *
     * @return If the module is locked successfully, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_Lock();

    /**
     * Unlocks a locked module.
     *
     * @param password 16 byte master password. The default password is a string of 16 NULL
     *                 characters.
     * @return If the password is wrong, return UF_ERR_NOT_MATCH. If it is successful, return
     * UF_RET_SUCCESS.
     */
    public native UF_RET_CODE UF_Unlock(final byte[] password);

    /**
     * Changes the master password.
     * If you want to use the master password permanently, you have to call UF_Save for saving
     * master password on your module.
     *
     * @param newPassword 16 byte new password.
     * @param oldPassword 16 byte old password.
     * @return If the old password is wrong, return UF_ERR_NOT_MATCH. If it is successful, return UF_RET_SUCCESS.
     */
    public native UF_RET_CODE UF_ChangePassword(final byte[] newPassword, final byte[] oldPassword);

    // UNSUPPORTED
    // public native UF_RET_CODE UF_ReadChallengeCode(byte[] challengeCode);
    // public native UF_RET_CODE UF_WriteChallengeCode(final byte[] challengeCode);

    /**
     * Power off
     */

    /**
     * Programmatically turns off a module. This function is only available with SFM4000 series.
     *
     * @return The module is powered off successfully, return UF_RET_SUCCESS.
     */
    public native UF_RET_CODE UF_PowerOff();

    /**
     * System parameters
     */

    /**
     * To prevent redundant communication, the SFM SDK caches the system parameters previously
     * read or written. UF_InitSysParameter clears this cache. It is called in UF_Reconnect.
     */
    public native void UF_InitSysParameter();

    /**
     * Reads the value of a system parameter.
     *
     * @param parameter System parameter to be read.
     * @param value     The value of the specified system parameter to be returned.
     * @return If the function succeeds, return UF_RET_SUCCESS. If there is no such parameter,
     * return UF_ERR_NOT_FOUND. Otherwise, return the corresponding error code.
     */
    public native UF_RET_CODE UF_GetSysParameter(UF_SYS_PARAM parameter, int[] value);

    /**
     * Writes the value of a system parameter. The parameter value is changed in memory only. To
     * make the change permanent, UF_Save should be called after this function. For BioEntry
     * Smart and Pass, users cannot change the UF_SYS_MODULE_ID system parameter.
     *
     * @param parameter System parameter to be written.
     * @param value     Value of the system parameter. Refer to the Packet Protocol Manual for
     *                  available values for each parameter.
     * @return If the function succeeds, return UF_RET_SUCCESS. If there is no such parameter,
     * return UF_ERR_NOT_FOUND. Otherwise, return the corresponding error code.
     */
    public native UF_RET_CODE UF_SetSysParameter(UF_SYS_PARAM parameter, int value);

    /**
     * Reads the values of multiple system parameters.
     *
     * @param parameterCount Number of system parameters to be read.
     * @param parameters     Array of system parameters to be read.
     * @param values         Array of the values of the specified system parameters to be read.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_GetMultiSysParameter(int parameterCount, UF_SYS_PARAM[] parameters, int[] values);

    /**
     * Writes the values of multiple system parameters. The parameter value is changed in memory
     * only. To make the change permanent, UF_Save should be called.
     *
     * @param parameterCount Number of system parameters to be written.
     * @param parameters     Array of system parameters to be written.
     * @param values         Array of the values of the specified system parameters to be written.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_SetMultiSysParameter(int parameterCount, UF_SYS_PARAM[] parameters, int[] values);

    /**
     * Saves the system parameters into the flash memory.
     *
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_Save();


    /**
     * Template management
     */

    /**
     * Gets the number of templates stored in the module.
     *
     * @param numOfTemplate The number of templates to be returned.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_GetNumOfTemplate(int[] numOfTemplate);

    /**
     * Gets the template capacity of the module.
     *
     * @param maxNumOfTemplate The template capacity to be returned.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_GetMaxNumOfTemplate(int[] maxNumOfTemplate);

    /**
     * Retrieves all the user and template information stored in the module.
     *
     * @param userInfo      Array of UFUserInfo structures, which will store all the information. This
     *                      pointer should be preallocated large enough to store all the information.
     * @param numOfUser     The number of users to be returned.
     * @param numOfTemplate The number of templates to be returned.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_GetAllUserInfo(UFUserInfo[] userInfo, int[] numOfUser, int[] numOfTemplate);

    /**
     * Retrieves all the user and template information stored in the BioEntry reader.
     *
     * @param userInfo      Array of UFUserInfoEx structures, which will store all the information.
     *                      This pointer should be preallocated large enough to store all the
     *                      information.
     * @param numOfUser     The number of users to be returned.
     * @param numOfTemplate The number of templates to be returned.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_GetAllUserInfoEx(UFUserInfoEx[] userInfo, int[] numOfUser, int[] numOfTemplate);

    // UNSUPPORTED
    // public native void UF_SortUserInfo(UFUserInfo[] userInfo, int numOfUser);

    /**
     * Sets the administration level of a user. See UF_EnrollAfterVerification and
     * UF_DeleteAllAfterVerificatoin for usage of administration level.
     *
     * @param userID     User ID.
     * @param adminLevel Specifies the administration level of the user.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_SetAdminLevel(int userID, UF_ADMIN_LEVEL adminLevel);

    /**
     * Gets the administration level of a user.
     *
     * @param userID     User ID.
     * @param adminLevel The administration level of the user to be returned.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_GetAdminLevel(int userID, UF_ADMIN_LEVEL[] adminLevel);

    /**
     * The security level can be assigned per user basis for 1:1 matching. 1:N matching –
     * identification – is not affected by this setting.
     *
     * @param userID        User ID.
     * @param securityLevel
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_SetSecurityLevel(int userID, UF_USER_SECURITY_LEVEL securityLevel);

    /**
     * Gets the security level of a user.
     *
     * @param userID        User ID.
     * @param securityLevel The security level of the user to be returned.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_GetSecurityLevel(int userID, UF_USER_SECURITY_LEVEL[] securityLevel);

    /**
     * Resets administration levels of all users to UF_ADMIN_NONE.
     *
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_ClearAllAdminLevel();

    /**
     * Saves all the templates and user information stored in a module into the specified file.
     *
     * @param fileName A string that specifies the file name.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_SaveDB(final String fileName);

    /**
     * Loads templates and user information from the specified file. All the templates previously
     * stored in the module will be erased before loading the DB.
     *
     * @param fileName A string that specifies the file name.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the corresponding error code.
     */
    public native UF_RET_CODE UF_LoadDB(final String fileName);

    /**
     * Checks if the specified user ID has enrolled templates.
     *
     * @param userID        User ID.
     * @param numOfTemplate The number of templates of the user ID to be returned.
     * @return If there are templates of the user ID, return UF_RET_SUCCESS. Otherwise, return
     * the corresponding error code.
     */
    public native UF_RET_CODE UF_CheckTemplate(int userID, int[] numOfTemplate);

    /**
     * Reads the templates of the specified user ID.
     *
     * @param userID        User ID.
     * @param numOfTemplate The number of templates of the user ID to be returned.
     * @param templateData  The template data to be returned. This pointer should be preallocated
     *                      large enough to store all the template data.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_ReadTemplate(int userID, int[] numOfTemplate, byte[] templateData);

    /**
     * Reads one template of the specified user ID.
     *
     * @param userID       User ID.
     * @param subID        Sub index of the template. It is between 0 and 9.
     * @param templateData The template data to be returned. This pointer should be preallocated
     *                     large enough to store all the template data.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_ReadOneTemplate(int userID, int subID, byte[] templateData);

    /**
     * Scans a fingerprint on the sensor and receives the template of it.
     *
     * @param templateData The template data to be returned.
     * @param templateSize The template size to be returned.
     * @param imageQuality The image quality score to be returned. The score shows the quality of
     *                     scanned fingerprint and is in the range of 0 ~ 100.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_ScanTemplate(byte[] templateData, int[] templateSize, int[] imageQuality);

    /**
     * UF_SYS_PROVISIONAL_ENROLL determines if enrolled templates are saved permanently into
     * flash memory or temporarily into DRAM. With provisional enroll, enrolled templates on DRAM
     * will be erased if the module is turned off. UF_FixProvisionalTemplate saves the
     * provisional templates into the flash memory.
     *
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_FixProvisionalTemplate();

    /**
     * Sets the authentication type of a user. UF_AUTH_BYPASS can be used for 1:1 matching, when
     * it is necessary to allow access without matching fingerprints. UF_AUTH_REJECT can be used
     * for disabling some IDs temporarily. The default authentication mode is UF_AUTH_FINGERPRINT
     * .
     *
     * @param userID   User ID.
     * @param authType Specifies the authentication type of the user.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_SetAuthType(int userID, UF_AUTH_TYPE authType);

    /**
     * Gets the authentication type of a user.
     *
     * @param userID   User ID.
     * @param authType The authentication type of the user to be returned.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_GetAuthType(int userID, UF_AUTH_TYPE[] authType);

    /**
     * Receive user IDs with the specified authentication type.
     *
     * @param authType Authentication type.
     * @param numOfID  The number of user IDs to be returned.
     * @param userID   Array of user IDs which have the specified authentication type.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_GetUserIDByAuthType(UF_AUTH_TYPE authType, int[] numOfID, int[] userID);

    /**
     * Resets the authentication types of all users to UF_AUTH_FINGERPRINT.
     *
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_ResetAllAuthType();

    /**
     * Adds a user ID to the blacklist.
     *
     * @param userID             User ID.
     * @param numOfBlacklistedID Number of IDs in the blacklist after adding.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_AddBlacklist(int userID, int[] numOfBlacklistedID);

    /**
     * Deletes an ID from the blacklist.
     *
     * @param userID             User ID.
     * @param numOfBlacklistedID Number of IDs in the blacklist after deleting.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_DeleteBlacklist(int userID, int[] numOfBlacklistedID);

    /**
     * Receive user IDs in the blacklist.
     *
     * @param numOfBlacklistedID The number of IDs in the blacklist.
     * @param userID             Array of user IDs in the blacklist. This should be pre-acllocated large
     *                           enough.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_GetBlacklist(int[] numOfBlacklistedID, int[] userID);

    /**
     * Clears the blacklist.
     *
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_DeleteAllBlacklist();

    /**
     * Specifies how many times the user is permitted to access per day. The available options
     * are between 0 and 7. The default value is 0, which means that there is no limit. If the
     * user tries to authenticate after the limit is reached, UF_ERR_EXCEED_ENTRANCE_LIMIT error
     * will be returned.
     *
     * @param userID        User ID.
     * @param entranceLimit Entrance limit between 0 and 7.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_SetEntranceLimit(int userID, int entranceLimit);

    /**
     * Gets the entrance limit of a user.
     *
     * @param userID        User ID.
     * @param entranceLimit Pointer to the entrance limit of the user.
     * @param entranceCount The number of entrance for today. This count is reset to 0 at midnight.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_GetEntranceLimit(int userID, int[] entranceLimit, int[] entranceCount);

    /**
     * Resets the entrance limits of all users to 0 – infinite.
     *
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_ClearAllEntranceLimit();

    /**
     * Image
     */

    /**
     * Converts a UFImage into a bitmap and save it into the specified file.
     *
     * @param fileName A string that specifies the file name.
     * @param image    The UFImage to be saved.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_SaveImage(final String fileName, UFImage image);

    /**
     * Loads a bmp file into a UFImage structure.
     *
     * @param fileName A string that specifies the file name.
     * @param image    The UFImage structure.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_LoadImage(final String fileName, UFImage image);

    /**
     * Reads the last scanned fingerprint image.
     *
     * @param image The UFImage structure.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_ReadImage(UFImage image);

    /**
     * Scans a fingerprint input on the sensor and retrieves the image of it.
     *
     * @param image The UFImage structure.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_ScanImage(UFImage image);


    /**
     * Identify
     */

    /**
     * Identifies the fingerprint input on the sensor.
     *
     * @param userID The user ID to be returned.
     * @param subID  The index of the template to be returned.
     * @return If matching succeeds, return UF_RET_SUCCESS. If matching fails, return
     * UF_ERR_NOT_FOUND. Otherwise, return the corresponding error code.
     */
    public native UF_RET_CODE UF_Identify(int[] userID, byte[] subID);

    /**
     * Identifies a template.
     *
     * @param templateSize Size of the template data.
     * @param templateData The template data.
     * @param userID       The user ID to be returned
     * @param subID        The index of the template to be returned.
     * @return If matching succeeds, return UF_RET_SUCCESS. If matching fails, return
     * UF_ERR_NOT_FOUND. Otherwise, return the corresponding error code.
     */
    public native UF_RET_CODE UF_IdentifyTemplate(int templateSize, byte[] templateData, int[] userID, byte[] subID);

    /**
     * Identifies a fingerprint image.
     *
     * @param imageSize Size of the image data.
     * @param imageData The raw image data. Note that it is not the pointer to UFImage, but the
     *                  pointer to the raw pixel data without the UFImage header.
     * @param userID    The user ID to be returned.
     * @param subID     The index of the template to be returned.
     * @return If matching succeeds, return UF_RET_SUCCESS. If matching fails, return
     * UF_ERR_NOT_FOUND. Otherwise, return the corresponding error code.
     */
    public native UF_RET_CODE UF_IdentifyImage(int imageSize, byte[] imageData, int[] userID, byte[] subID);

    /**
     * Verify
     */

    /**
     * Verifies if a fingerprint input on the sensor matches the enrolled fingerprints of the
     * specified user id.
     *
     * @param userID User ID.
     * @param subID  The index of the template to be returned.
     * @return If matching succeeds, return UF_RET_SUCCESS. If matching fails, return
     * UF_ERR_NOT_MATCH. Otherwise, return the corresponding error code.
     */
    public native UF_RET_CODE UF_Verify(int userID, byte[] subID);

    /**
     * Verifies a template.
     *
     * @param templateSize Size of the template data.
     * @param templateData The template data to be sent.
     * @param userID       User ID.
     * @param subID        The index of the template to be returned.
     * @return If matching succeeds, return UF_RET_SUCCESS. If matching fails, return
     * UF_ERR_NOT_MATCH. Otherwise, return the corresponding error code.
     */
    public native UF_RET_CODE UF_VerifyTemplate(int templateSize, byte[] templateData, int userID, byte[] subID);

    /**
     * Transmits fingerprint templates from the host to the module and verifies if they match the live fingerprint input on the sensor.
     *
     * @param numOfTemplate Number of templates to be transferred to the module.
     * @param templateSize  Size of a template.
     * @param templateData  The template data to be transferred to the module.
     * @return If matching succeeds, return UF_RET_SUCCESS. If matching fails, return
     * UF_ERR_NOT_MATCH. Otherwise, return the corresponding error code.
     */
    public native UF_RET_CODE UF_VerifyHostTemplate(int numOfTemplate, int templateSize, byte[] templateData);

    /**
     * Verifies a fingerprint image.
     *
     * @param imageSize Size of the fingerprint image.
     * @param imageData The raw image data. Note that it is not the pointer to UFImage, but
     *                  the pointer to the raw pixel data without the UFImage header.
     * @param userID    User ID.
     * @param subID     The index of the template to be returned.
     * @return If matching succeeds, return UF_RET_SUCCESS. If matching fails, return
     * UF_ERR_NOT_MATCH. Otherwise, return the corresponding error code.
     */
    public native UF_RET_CODE UF_VerifyImage(int imageSize, byte[] imageData, int userID, byte[] subID);

    /**
     * Enroll
     */

    /**
     * Enrolls fingerprint inputs on the sensor. The enrollment process varies according to the
     * UF_SYS_ENROLL_MODE system parameter.
     *
     * @param userID       User ID.
     * @param option       Enroll option.
     * @param enrollID     The enrolled user ID. This parameter can be different from userIDwhen
     *                     AUTO_ID option is used.
     * @param imageQuality The image quality score to be returned. The score shows the quality of
     *                     scanned fingerprint and is in the range of 0 ~ 100.
     * @return If enroll succeeds, return UF_RET_SUCCESS. Otherwise, return the corresponding
     * error code.
     */
    public native UF_RET_CODE UF_Enroll(int userID, UF_ENROLL_OPTION option, int[] enrollID, int[] imageQuality);

    /**
     * Continues the enrollment process when the enroll mode is UF_ENROLL_TWO_TIMES2 or
     * UF_ENROLL_TWO_TEMPLATES2.
     *
     * @param userID       User ID.
     * @param enrollID     The enrolled user ID. This parameter can be different from userID
     *                     when AUTO_ID option is used.
     * @param imageQuality The image quality score to be returned. The score shows the quality
     *                     of scanned fingerprint and is in the range of 0 ~ 100.
     * @return If enroll succeeds, return UF_RET_SUCCESS. Otherwise, return the corresponding
     * error code.
     */
    public native UF_RET_CODE UF_EnrollContinue(int userID, int[] enrollID, int[] imageQuality);

    /**
     * Enroll and Delete functions change the fingerprint DB stored in the module. For some
     * applications, it might be necessary to obtain administrator’s permission before enrolling
     * or deleting fingerprints. To process these functions, a user with proper administration
     * level should verify himself first. If there is no user with corresponding administration
     * level, these commands will fail with UF_ERR_UNSUPPORTED error code. If the verification
     * fails, UF_ERR_NOT_MATCH error code will be returned. The only exception is that
     * UF_EnrollAfterVerification will succeed when the fingerprint DB is empty. In that case,
     * the first user enrolled by UF_EnrollAfterVerification will have UF_ADMIN_LEVEL_ALL.
     *
     * @param userID       User ID.
     * @param option       Enroll option.
     * @param enrollID     The enrolled user ID. This parameter can be different from userID when
     *                     AUTO_ID option is used.
     * @param imageQuality The image quality score to be returned. The score shows the quality of
     *                     scanned fingerprint and is in the range of 0 ~ 100.
     * @return If enroll succeeds, return UF_RET_SUCCESS. If there is no user with corresponding
     * administration level, return UF_ERR_UNSUPPORTED. If administrator’s verification fails,
     * return UF_ERR_NOT_MATCH. Otherwise, return the corresponding error code.
     */
    public native UF_RET_CODE UF_EnrollAfterVerification(int userID, UF_ENROLL_OPTION option, int[] enrollID, int[] imageQuality);

    /**
     * Enrolls a fingerprint template.
     *
     * @param userID       User ID.
     * @param option       Enroll option.
     * @param templateSize Size of the template data.
     * @param templateData The template data.
     * @param enrollID     The enrolled user ID. This parameter can be different from userID when
     *                     AUTO_ID option is used.
     * @return If enroll succeeds, return UF_RET_SUCCESS. Otherwise, return the corresponding
     * error code.
     */
    public native UF_RET_CODE UF_EnrollTemplate(int userID, UF_ENROLL_OPTION option, int templateSize, byte[] templateData, int[] enrollID);

    /**
     * Enrolls multiple templates to the specified ID.
     *
     * @param userID        User ID.
     * @param option        Enroll option.
     * @param numOfTemplate Number of templates to be enrolled.
     * @param templateSize  Size of one template data. For example, when enroll 3 templates of 384
     *                      byte, this parameter is 384 not 1152.
     * @param templateData  The template data.
     * @param enrollID      The enrolled user ID. This parameter can be different from userID when
     *                      AUTO_ID option is used.
     * @return If enroll succeeds, return UF_RET_SUCCESS. Otherwise, return the corresponding
     * error code.
     */
    public native UF_RET_CODE UF_EnrollMultipleTemplates(int userID, UF_ENROLL_OPTION option, int numOfTemplate, int templateSize, byte[] templateData, int[] enrollID);

    /**
     * Enrolls multiple templates to the specified ID.
     *
     * @param userID        User ID.
     * @param option        Enroll option.
     * @param numOfTemplate Number of tempaltes to be enrolled.
     * @param numOfEnroll   Number of enroll.
     * @param templateSize  Size of one template data. For example, when enroll 3 templates of 384
     *                      byte, this parameter is 384 not 1152.
     * @param templateData  The template data.
     * @param enrollID      The enrolled user ID. This parameter can be different from userID when
     *                      AUTO_ID option is used.
     * @return If enroll succeeds, return UF_RET_SUCCESS. Otherwise, return the corresponding
     * error code.
     */
    public native UF_RET_CODE UF_EnrollMultipleTemplatesEx(int userID, UF_ENROLL_OPTION option, int numOfTemplate, int numOfEnroll, int templateSize, byte[] templateData, int[] enrollID);


    /**
     * Enrolls a fingerprint image.
     *
     * @param userID       User ID.
     * @param option       Enroll option.
     * @param imageSize    Size of the image data.
     * @param imageData    The raw image data. Note that it is not the pointer to UFImage, but
     *                     the pointer to the raw pixel data without the UFImage header.
     * @param enrollID     The enrolled user ID. This parameter can be different from userID
     *                     when AUTO_ID option is used.
     * @param imageQuality The image quality score to be returned. The score shows the quality of
     *                     scanned fingerprint and is in the range of 0 ~ 100.
     * @return If enroll succeeds, return UF_RET_SUCCESS. Otherwise, return the corresponding
     * error code.
     */
    public native UF_RET_CODE UF_EnrollImage(int userID, UF_ENROLL_OPTION option, int imageSize, byte[] imageData, int[] enrollID, int[] imageQuality);

    /**
     * Delete
     */

    /**
     * Deletes the enrolled templates of the specified user ID.
     *
     * @param userID User ID.
     * @return If delete succeeds, return UF_RET_SUCCESS. Otherwise, return the corresponding
     * error code.
     */
    public native UF_RET_CODE UF_Delete(int userID);

    /**
     * Deletes one template of the specified user ID.
     *
     * @param userID User ID.
     * @param subID  Sub index of the template. It is between 0 and 9.
     * @return If delete succeeds, return UF_RET_SUCCESS. Otherwise, return the corresponding
     * error code.
     */
    public native UF_RET_CODE UF_DeleteOneTemplate(int userID, int subID);

    /**
     * Deletes the enrolled templates of multiple user IDs.
     *
     * @param startUserID   First user ID to be deleted.
     * @param lastUserID    Last user ID to be deleted.
     * @param deletedUserID The number of IDs to be actually deleted by the module.
     * @return If delete succeeds, return UF_RET_SUCCESS. Otherwise, return the corresponding
     * error code.
     */
    public native UF_RET_CODE UF_DeleteMultipleTemplates(int startUserID, int lastUserID, int[] deletedUserID);

    /**
     * Deletes all the templates stored in a module.
     *
     * @return If delete succeeds, return UF_RET_SUCCESS. Otherwise, return the corresponding error code.
     */
    public native UF_RET_CODE UF_DeleteAll();

    /**
     * Deletes all the templates after administrator’s verification.
     *
     * @return If delete succeeds, return UF_RET_SUCCESS. If there is no user with corresponding
     * administration level, return UF_ERR_UNSUPPORTED. If administrator’s verification fails,
     * return UF_ERR_NOT_MATCH. Otherwise, return the corresponding error code.
     */
    public native UF_RET_CODE UF_DeleteAllAfterVerification();


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// UNSUPPORTED
//
//    /**
//     * IO for SFM3500/SFM5500
//     */
//
//
//    public native void UF_InitIO();
//    public native UF_RET_CODE UF_SetInputFunction(UF_INPUT_PORT port, UF_INPUT_FUNC inputFunction, int minimumTime);
//    public native UF_RET_CODE UF_GetInputFunction(UF_INPUT_PORT port, UF_INPUT_FUNC[] inputFunction, int[] minimumTime);
//    public native UF_RET_CODE UF_GetInputStatus(UF_INPUT_PORT port, boolean remainStatus, int[] status);
//    public native UF_RET_CODE UF_GetOutputEventList(UF_OUTPUT_PORT port, UF_OUTPUT_EVENT[] events, int[] numOfEvent);
//    public native UF_RET_CODE UF_ClearAllOutputEvent(UF_OUTPUT_PORT port);
//    public native UF_RET_CODE UF_ClearOutputEvent(UF_OUTPUT_PORT port, UF_OUTPUT_EVENT event);
//    public native UF_RET_CODE UF_SetOutputEvent(UF_OUTPUT_PORT port, UF_OUTPUT_EVENT event, UFOutputSignal signal);
//    public native UF_RET_CODE UF_GetOutputEvent(UF_OUTPUT_PORT port, UF_OUTPUT_EVENT event, UFOutputSignal[] signal);
//    public native UF_RET_CODE UF_SetOutputStatus(UF_OUTPUT_PORT port, boolean status);
//    public native UF_RET_CODE UF_SetLegacyWiegandConfig(boolean enableInput, boolean enableOutput, int fcBits, int fcCode);
//    public native UF_RET_CODE UF_GetLegacyWiegandConfig(boolean[] enableInput, boolean[] enableOutput, int[] fcBits, int[] fcCode);
//    public native UF_RET_CODE UF_MakeIOConfiguration(UFConfigComponentHeader[] configHeader, byte[] configData);
//
//    /**
//     * IO for SFM3000/SFM5000/SFM6000
//     */
//    public native UF_RET_CODE UF_GetGPIOConfiguration(UF_GPIO_PORT port, UF_GPIO_MODE[] mode, int[] numOfData, UFGPIOData[] data);
//    public native UF_RET_CODE UF_SetInputGPIO(UF_GPIO_PORT port, UFGPIOInputData data);
//    public native UF_RET_CODE UF_SetOutputGPIO(UF_GPIO_PORT port, int numOfData, UFGPIOOutputData[] data);
//    public native UF_RET_CODE UF_SetBuzzerGPIO(UF_GPIO_PORT port, int numOfData, UFGPIOOutputData[] data);
//    public native UF_RET_CODE UF_SetSharedGPIO(UF_GPIO_PORT port, UFGPIOInputData inputData, int numOfOutputData, UFGPIOOutputData[] outputData);
//    public native UF_RET_CODE UF_DisableGPIO(UF_GPIO_PORT port);
//    public native UF_RET_CODE UF_ClearAllGPIO();
//    public native UF_RET_CODE UF_SetDefaultGPIO();
//    public native UF_RET_CODE UF_EnableWiegandInput(UFGPIOWiegandData data);
//    public native UF_RET_CODE UF_EnableWiegandOutput(UFGPIOWiegandData data);
//    public native UF_RET_CODE UF_DisableWiegandInput();
//    public native UF_RET_CODE UF_DisableWiegandOutput();
//    public native UF_RET_CODE UF_MakeGPIOConfiguration(UFConfigComponentHeader[] configHeader, byte[] configData);
//
//    /**
//     * User memory
//     */
//    public native UF_RET_CODE UF_WriteUserMemory(byte[] memory);
//    public native UF_RET_CODE UF_ReadUserMemory(byte[] memory);
//
//    /**
//     * Log and time management
//     */
//    public native UF_RET_CODE UF_SetTime(time_t timeVal);
//    public native UF_RET_CODE UF_GetTime(time_t[] timeVal);
//    public native UF_RET_CODE UF_GetNumOfLog(int[] numOfLog, int[] numOfTotalLog);
//    public native UF_RET_CODE UF_ReadLog(int startIndex, int count, UFLogRecord[] logRecord, int[] readCount);
//    public native UF_RET_CODE UF_ReadLatestLog(int count, UFLogRecord[] logRecord, int[] readCount);
//    public native UF_RET_CODE UF_DeleteOldestLog(int count, int[] deletedCount);
//    public native UF_RET_CODE UF_DeleteAllLog();
//    public native UF_RET_CODE UF_ClearLogCache();
//    public native UF_RET_CODE UF_ReadLogCache(int dataPacketSize, int[] numOfLog, UFLogRecord[] logRecord);
//    public native UF_RET_CODE UF_SetCustomLogField(UF_LOG_SOURCE source, int customField);
//    public native UF_RET_CODE UF_GetCustomLogField(UF_LOG_SOURCE source, int[] customField);
//
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Upgrade
     */

    /**
     * Upgrades the firmware of the module. Users should not turn off the module when upgrade is
     * in progress.
     *
     * @param firmwareFilename A string that specifies the firmware file name.
     * @param dataPacketSize   The packet size of firmware data. If it is 16384, the firmware is
     *                         divided into 16384 byte packets before transferring to the module.
     * @return If upgrade succeeds, return UF_RET_SUCCESS. Otherwise, return the corresponding
     * error code.
     */
    public native UF_RET_CODE UF_Upgrade(final String firmwareFilename, int dataPacketSize);

    // UNSUPPORTED
    // public native UF_RET_CODE UF_UpgradeEx(final byte[] firmwareFilename, UF_UPGRADE_OPTION option, int dataPacketSize);
    // public native UF_RET_CODE UF_DFU_Upgrade();

    /**
     * File System
     */
    /**
     * Erase the user database partition (which is specific area stores fingerprint template into flash
     * memory of the module.
     *
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_FormatUserDatabase();

    /**
     * Reset all system parameter and GPIO configuration of the module as factory default.
     *
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
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

    /**
     * Decode the compressed WSQ fingerprint image which is read or scaned by the module.
     *
     * @param odata     The decoded image data. (odata is dynamic allocated by the UF_WSQ_Decode
     *                  function. You should free memory of odata after use it.)
     * @param ow        The width of the decoded image data.
     * @param oh        The height of the decoded image data.
     * @param od        The bit depth of the decoded image data. (Always returns 8)
     * @param oppi      The PPI (pixel per inch) of the decoded data.
     * @param lossyflag The lossy flag (Always returns 1)
     * @param idata     The read or scanned WSQ fingerprint image data by the module.
     * @param ilen      The lengh of idata.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_WSQ_Decode(byte[] odata, int[] ow, int[] oh, int[] od, int[] oppi, int[] lossyflag, byte[] idata, final int ilen);

    /**
     * Retrieves the last scanned fingerprint image.
     *
     * @param image      The UFImage structure.
     * @param type       Type of an imgae to read or scan.
     * @param wsqBitRate WSQ compression bit rate.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_ReadImageEx(UFImage image, UF_IMAGE_TYPE type, int wsqBitRate);

    /**
     * Scans a fingerprint on the sensor and retrieves the image data.
     *
     * @param image      The UFImage structure.
     * @param type       Type of an imgae to read or scan.
     * @param wsqBitRate WSQ compression bit rate.
     * @return If the function succeeds, return UF_RET_SUCCESS. Otherwise, return the
     * corresponding error code.
     */
    public native UF_RET_CODE UF_ScanImageEx(UFImage image, UF_IMAGE_TYPE type, int wsqBitRate);

    /**
     * Test
     */

    /**
     * Initialize callback functions from the JNI.
     */
    private static native void InitCallbackFunctions();


    static {

        System.loadLibrary("native-lib");
        InitCallbackFunctions();
    }

}

