package com.supremainc.sfm_sdk_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.supremainc.sfm_sdk.MessageHandler;
import com.supremainc.sfm_sdk.SFM_SDK_ANDROID;
import com.supremainc.sfm_sdk.SFM_SDK_ANDROID_CALLBACK_INTERFACE;
import com.supremainc.sfm_sdk.UF_ENROLL_MODE;
import com.supremainc.sfm_sdk.UF_ENROLL_OPTION;
import com.supremainc.sfm_sdk.UF_IMAGE_TYPE;
import com.supremainc.sfm_sdk.UF_SYS_PARAM;
import com.supremainc.sfm_sdk.UsbService;
import com.supremainc.sfm_sdk.enumeration.UF_ADMIN_LEVEL;
import com.supremainc.sfm_sdk.enumeration.UF_AUTH_TYPE;
import com.supremainc.sfm_sdk.enumeration.UF_PROTOCOL;
import com.supremainc.sfm_sdk.enumeration.UF_RET_CODE;
import com.supremainc.sfm_sdk.enumeration.UF_USER_SECURITY_LEVEL;
import com.supremainc.sfm_sdk.structure.UFImage;
import com.supremainc.sfm_sdk.structure.UFModuleInfo;
import com.supremainc.sfm_sdk.structure.UFUserInfo;
import com.supremainc.sfm_sdk.structure.UFUserInfoEx;

import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
//import com.supremainc.sfm_sdk.MessageHandler;
//import com.supremainc.sfm_sdk.SFM_SDK_ANDROID;
//import com.supremainc.sfm_sdk.UsbService;

//import com.supremainc.sfm_sdk.MessageHandler;
//import com.supremainc.sfm_sdk.SFM_SDK_ANDROID;
//import com.supremainc.sfm_sdk.UsbService;


public class MainActivity extends AppCompatActivity {

    private TextView display;
    private EditText editText;
    private ImageView imageView;

    private SFM_SDK_ANDROID sdk;


    /*
     * Notifications from UsbService will be received here.
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();

                    String version = "SDK Version : " + sdk.UF_GetSDKVersion() + "\n";
                    display.append(version);

                    sdk.UF_InitSysParameter();
//
                    UF_RET_CODE result = sdk.UF_InitCommPort(115200, false);
//
//                    String ret = result.toString();
//
//                    Log.i("[INFO]",version);
//
//                    if(result == UF_RET_CODE.UF_RET_SUCCESS)
//                    {
////                        long id = sdk.UF_GetModuleID();
////                        Log.i("[INFO]", String.format("Module ID %d", id));
////
//                        int[] value = new int[10];
//                        result = sdk.UF_GetSysParameter(UF_SYS_PARAM.UF_SYS_BAUDRATE, value );
////
//                        Log.i("[INFO]", String.format("Param %d", value[0]));
//                    }


//                    sdk.UF_InitCommPort(115200, true);

//                    Test_Identify();

                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private final MessageHandler mHandler = new MessageHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = "[RECV] " + (String) msg.obj + "\n";
                    display.append(data);
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE", Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE", Toast.LENGTH_LONG).show();
                    break;
                case UsbService.SYNC_READ:
                    String buffer = (String) msg.obj;
                    display.append(buffer);


            }
        }
    };

    String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for (final byte b : a)
            sb.append(String.format("%02X ", b & 0xff));
        return sb.toString();
    }

    String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
        Date time = new Date();

        return format.format(time);
    }

    SFM_SDK_ANDROID.SendPacketCallback sendPacketCallback = new SFM_SDK_ANDROID.SendPacketCallback() {
        @Override
        public void callback(byte[] data) {
            display.post(new Runnable() {
                @Override
                public void run() {
                    final String str = getCurrentTime() + " - [SEND] " + byteArrayToHex(data) + "\n";
                    display.append(str);
                }
            });
        }
    };

    SFM_SDK_ANDROID.ReceivePacketCallback receivePacketCallback = new SFM_SDK_ANDROID.ReceivePacketCallback() {
        @Override
        public void callback(byte[] data) {
            display.post(new Runnable() {
                @Override
                public void run() {
                    final String str = getCurrentTime() + " - [RECV] " + byteArrayToHex(data) + "\n";
                    display.append(str);
                }
            });
        }
    };

    SFM_SDK_ANDROID.SendDataPacketCallback sendDataPacketCallback = new SFM_SDK_ANDROID.SendDataPacketCallback() {
        @Override
        public void callback(int index, int numOfPacket) {
            display.post(new Runnable() {
                @Override
                public void run() {
                    final String str = getCurrentTime() + String.format(" %d / %d written\n", index, numOfPacket);
                    display.append(str);
                }
            });
        }
    };

    SFM_SDK_ANDROID.ReceiveDataPacketCallback receiveDataPacketCallback = new SFM_SDK_ANDROID.ReceiveDataPacketCallback() {
        @Override
        public void callback(int index, int numOfPacket) {

        }
    };

    SFM_SDK_ANDROID.SendRawDataCallback sendRawDataCallback = new SFM_SDK_ANDROID.SendRawDataCallback() {
        @Override
        public void callback(int writtenLen, int totalSize) {

        }
    };

    SFM_SDK_ANDROID.ReceiveRawDataCallback receiveRawDataCallback = new SFM_SDK_ANDROID.ReceiveRawDataCallback() {
        @Override
        public void callback(int readLen, int totalSize) {

        }
    };

    SFM_SDK_ANDROID.MsgCallback msgCallback = new SFM_SDK_ANDROID.MsgCallback() {
        @Override
        public boolean callback(byte message) {
            display.post(new Runnable() {
                @Override
                public void run() {
                    display.append(String.format("CommandEx callback : 0x%02X\n", message));
                }
            });

            if (message == 0x62)
                return false;
            else
                return true;
        }
    };

    SFM_SDK_ANDROID.UserInfoCallback userInfoCallback = new SFM_SDK_ANDROID.UserInfoCallback() {
        @Override
        public void callback(int index, int numOfTemplate) {
        }
    };

    SFM_SDK_ANDROID.ScanCallback scanCallback = new SFM_SDK_ANDROID.ScanCallback() {
        @Override
        public void callback(byte errCode) {
            display.post(new Runnable() {
                @Override
                public void run() {
                    final String str = "Scan Success\n";
                    display.append(str);
                }
            });

        }
    };

    SFM_SDK_ANDROID.IdentifyCallback identifyCallback = new SFM_SDK_ANDROID_CALLBACK_INTERFACE.IdentifyCallback() {
        @Override
        public void callback(byte errCode) {
            display.post(new Runnable() {
                @Override
                public void run() {
                    display.append(String.format("IdentifyCallback : 0x%02X\n", errCode));
                }
            });

        }
    };

    SFM_SDK_ANDROID.VerifyCallback verifyCallback = new SFM_SDK_ANDROID_CALLBACK_INTERFACE.VerifyCallback() {
        @Override
        public void callback(byte errCode) {
            display.post(new Runnable() {
                @Override
                public void run() {
                    display.append(String.format("VerifyCallback : 0x%02X\n", errCode));
                }
            });
        }
    };

    SFM_SDK_ANDROID.EnrollCallback enrollCallback = new SFM_SDK_ANDROID_CALLBACK_INTERFACE.EnrollCallback() {
        @Override
        public void callback(byte errCode, UF_ENROLL_MODE enrollMode, int numOfSuccess) {
            display.post(new Runnable() {
                @Override
                public void run() {
                    display.append(String.format("EnrollCallback : 0x%02X\n", errCode));
                    display.append(enrollMode.toString() + String.format(" numOfSuccess : %d\n", numOfSuccess));
                }
            });
        }
    };

    SFM_SDK_ANDROID.DeleteCallback deleteCallback = new SFM_SDK_ANDROID_CALLBACK_INTERFACE.DeleteCallback() {
        @Override
        public void callback(byte errCode) {
            display.post(new Runnable() {
                @Override
                public void run() {
                    display.append(String.format("DeleteCallback : 0x%02X\n", errCode));
                }
            });
        }
    };

    SFM_SDK_ANDROID.SearchModuleCallback searchModuleCallback = new SFM_SDK_ANDROID_CALLBACK_INTERFACE.SearchModuleCallback() {
        @Override
        public void callback(String comPort, int baudrate) {
            display.post(new Runnable() {
                @Override
                public void run() {
                    display.append(comPort + String.format("  baudrate : %d\n", baudrate));
                }
            });
        }
    };

    private void Test_Basic_Packet_Interface() {
        UF_RET_CODE ret;
        // UF_Reconnect
        sdk.UF_Reconnect();

        // Callback test
        sdk.UF_SetSendPacketCallback(sendPacketCallback);
        sdk.UF_SetReceivePacketCallback(receivePacketCallback);
        sdk.UF_SetSendDataPacketCallback(sendDataPacketCallback);
        sdk.UF_SetReceiveDataPacketCallback(receiveDataPacketCallback);
        sdk.UF_SetSendRawDataCallback(sendRawDataCallback);
        sdk.UF_SetReceiveRawDataCallback(receiveRawDataCallback);

        // UF_SetBaudrate
        ret = sdk.UF_SetBaudrate(115200);
        Log.d("UF_SetBaudrate", ret.toString());

        // UF_SetAsciiMode
        sdk.UF_SetAsciiMode(false);

        // UF_SendPacket
        ret = sdk.UF_SendPacket((byte) 0x04, 0, 0, (byte) 0, 1000);
        Log.d("UF_SendPacket", ret.toString());


        byte[] receivedPacket = new byte[15];
        // UF_ReceivePacket
        ret = sdk.UF_ReceivePakcet(receivedPacket, 1000);
        Log.d("UF_ReceivePacket", ret.toString());
        Log.d("UF_ReceivePacket", Arrays.toString(receivedPacket));


        // UF_SendNetworkPacket
        ret = sdk.UF_SendNetworkPacket((byte) 0x04, (short) 1, 0, 0, (byte) 0, 1000);
        Log.d("UF_SendNetworkPacket", ret.toString());

        // UF_ReceiveNetworkPacket
        ret = sdk.UF_ReceiveNetworkPakcet(receivedPacket, 1000);
        Log.d("UF_ReceiveNetworkPacket", ret.toString());
        Log.d("UF_ReceiveNetworkPacket", byteArrayToHex(receivedPacket));


        int[] value = new int[10];
        UF_RET_CODE result = sdk.UF_GetSysParameter(UF_SYS_PARAM.UF_SYS_BAUDRATE, value);
        display.post(new Runnable() {
            @Override
            public void run() {
                display.append(result.toString());
                display.append(String.format(" (0x%02X)", value[0]));
            }
        });

        // UF_SetDefaultPacketSize
        sdk.UF_SetDefaultPacketSize(1024);

        // UF_GetDefaultPacketSize
        int defaultPacketSize = sdk.UF_GetDefaultPacketSize();
        display.append(String.format("Default Packet Size %d\n", defaultPacketSize));
    }

    private void Test_Generic_Command_Interface() {
        UF_RET_CODE ret;
        // UF_Reconnect
        sdk.UF_Reconnect();

        // Callback test
        sdk.UF_SetSendPacketCallback(sendPacketCallback);
        sdk.UF_SetReceivePacketCallback(receivePacketCallback);
        sdk.UF_SetSendDataPacketCallback(sendDataPacketCallback);
        sdk.UF_SetReceiveDataPacketCallback(receiveDataPacketCallback);
        sdk.UF_SetSendRawDataCallback(sendRawDataCallback);
        sdk.UF_SetReceiveRawDataCallback(receiveRawDataCallback);

        // UF_SetBaudrate
        ret = sdk.UF_SetBaudrate(115200);
        Log.d("UF_SetBaudrate", ret.toString());

        // UF_SetAsciiMode
        sdk.UF_SetAsciiMode(false);

        // UF_GetProtocol
        UF_PROTOCOL protocol = sdk.UF_GetProtocol();
        int moduleID = sdk.UF_GetModuleID();
        Log.d("UF_GetProtocol : ", String.format("protocol : %s, module ID : %d", protocol.toString(), moduleID));
        // UF_SetProtocol
        sdk.UF_SetProtocol(UF_PROTOCOL.UF_NETWORK_PROTOCOL, 2);
        Log.d("UF_SetProtocol : ", String.format("UF_NETWORK_PROTOCOL, moduleID : 2"));
        // UF_GetProtocol
        protocol = sdk.UF_GetProtocol();
        moduleID = sdk.UF_GetModuleID();
        Log.d("UF_GetProtocol : ", String.format("protocol : %s, module ID : %d", protocol.toString(), moduleID));
        // UF_SetProtocol
        sdk.UF_SetProtocol(UF_PROTOCOL.UF_SINGLE_PROTOCOL, 1);
        Log.d("UF_SetProtocol : ", String.format("UF_SINGLE_PROTOCOL, moduleID : 1"));
        // UF_GetProtocol
        protocol = sdk.UF_GetProtocol();
        moduleID = sdk.UF_GetModuleID();
        Log.d("UF_GetProtocol : ", String.format("protocol : %s, module ID : %d", protocol.toString(), moduleID));

        // UF_SendPacket
        ret = sdk.UF_SendPacket((byte) 0x04, 0, 0, (byte) 0, 1000);
        Log.d("UF_SendPacket", ret.toString());


        byte[] receivedPacket = new byte[15];
        // UF_ReceivePacket
        ret = sdk.UF_ReceivePakcet(receivedPacket, 1000);
        Log.d("UF_ReceivePacket", ret.toString());
        Log.d("UF_ReceivePacket", Arrays.toString(receivedPacket));

        // UF_SetGenericCommandTimeout
        int old = sdk.UF_GetGenericCommandTimeout();
        Log.d("UF_GetGenericCommandTim", String.format("old generic command timeout : %d", old));

        // UF_SetGenericCommandTimeout
        sdk.UF_SetGenericCommandTimeout(10000);

        // UF_SetGenericCommandTimeout
        int changed = sdk.UF_GetGenericCommandTimeout();
        Log.d("UF_GetGenericCommandTim", String.format("changed generic command timeout : %d", changed));

        // UF_SetGenericCommandTimeout
        sdk.UF_SetGenericCommandTimeout(old);

        // UF_GetInputCommandTimeout
        old = sdk.UF_GetInputCommandTimeout();
        Log.d("UF_GetInputCommandTim", String.format("old input command timeout : %d", old));

        // UF_SetInputCommandTimeout
        sdk.UF_SetInputCommandTimeout(1000);

        // UF_GetInputCommandTimeout
        changed = sdk.UF_GetInputCommandTimeout();
        Log.d("UF_GetInputCommandTim", String.format("changed input command timeout : %d", changed));

        // UF_SetInputCommandTimeout
        sdk.UF_SetInputCommandTimeout(old);

        // UF_GetNetworkDelay
        old = sdk.UF_GetNetworkDelay();
        Log.d("UF_GetNetworkDelay", String.format("old network delay : %d", old));

        // UF_SetNetworkDelay
        sdk.UF_SetNetWorkDelay(1000);

        // UF_GetNetworkDelay
        changed = sdk.UF_GetNetworkDelay();
        Log.d("UF_GetNetworkDelay", String.format("changed network delay : %d", changed));

        // UF_SetNetworkDelay
        sdk.UF_SetNetWorkDelay(old);


        // Read all system parameters
        int[] vParam = new int[1];

        for (UF_SYS_PARAM iter : UF_SYS_PARAM.values()) {
            sdk.UF_GetSysParameter(iter, vParam);
            Log.d("UF_GetSysParam", iter.toString() + " : " + String.format("0x%02X", vParam[0]));

        }


        // UF_Command
        int[] param = new int[]{0};
        int[] size = new int[]{0};
        byte[] flag = new byte[]{(byte) 0x79};

//        ret = sdk.UF_Command((byte)0x04, param, size, flag);
//        Log.d("UF_Command", ret.toString());

        ret = sdk.UF_CommandEx((byte) 0x05, param, size, flag, msgCallback);
        Log.d("UF_CommandEx", ret.toString());
        Log.d("UF_CommandEx", String.format("%X %X %X", param[0], size[0], flag[0]));

//        ret = sdk.UF_Cancel(true);
//        Log.d("UF_Cancel", ret.toString());
//
//        byte[] data = new byte[384];
//
//        ret = sdk.UF_CommandSendData((byte)0x87, param, size, flag, data, 384);
//        Log.d("UF_CommandSendData", ret.toString());
//        Log.d("UF_CommandSendData", String.format("%d %d %d", param[0], size[0], flag[0]));
    }

    private void Test_System_Parameter() {
        UF_RET_CODE ret;
        // UF_Reconnect
        sdk.UF_Reconnect();

        // Callback test
        sdk.UF_SetSendPacketCallback(sendPacketCallback);
        sdk.UF_SetReceivePacketCallback(receivePacketCallback);
        sdk.UF_SetSendDataPacketCallback(sendDataPacketCallback);
        sdk.UF_SetReceiveDataPacketCallback(receiveDataPacketCallback);
        sdk.UF_SetSendRawDataCallback(sendRawDataCallback);
        sdk.UF_SetReceiveRawDataCallback(receiveRawDataCallback);

        // UF_SetBaudrate
        ret = sdk.UF_SetBaudrate(115200);
        Log.d("UF_SetBaudrate", ret.toString());

        // UF_SetAsciiMode
        sdk.UF_SetAsciiMode(false);

        int[] serialNumber = new int[]{0};
        sdk.UF_GetSysParameter(UF_SYS_PARAM.UF_SYS_TIMEOUT, serialNumber);
        Log.d("UF_GetSysParam", String.format("value : %02X", serialNumber[0]));

        sdk.UF_SetSysParameter(UF_SYS_PARAM.UF_SYS_TIMEOUT, serialNumber[0]);
        Log.d("UF_SetSysParam", String.format("value : %02X", serialNumber[0]));

        UF_SYS_PARAM[] parameters = new UF_SYS_PARAM[]{UF_SYS_PARAM.UF_SYS_AUX_BAUDRATE, UF_SYS_PARAM.UF_SYS_BUILD_NO, UF_SYS_PARAM.UF_SYS_FIRMWARE_VERSION};
        int[] values = new int[parameters.length];
        ret = sdk.UF_GetMultiSysParameter(parameters.length, parameters, values);
        Log.d("UF_GetMultiSysParameter", ret.toString());

        UF_SYS_PARAM[] parameters_set = new UF_SYS_PARAM[]{UF_SYS_PARAM.UF_SYS_FREE_SCAN, UF_SYS_PARAM.UF_SYS_ROTATION};
        int[] values_set = new int[]{0x30, 0x32};
        ret = sdk.UF_SetMultiSysParameter(parameters_set.length, parameters_set, values_set);
        Log.d("UF_SetMultiSysParameter", ret.toString());

        ret = sdk.UF_Save();
        Log.d("UF_Save", ret.toString());

    }


    private void Test_Module_Information() {
        final String TAG = "TEST_MODULE_INFORMATION";

        // UF_Reconnect
        sdk.UF_Reconnect();

        // Callback test
        sdk.UF_SetSendPacketCallback(sendPacketCallback);
        sdk.UF_SetReceivePacketCallback(receivePacketCallback);
        sdk.UF_SetSendDataPacketCallback(sendDataPacketCallback);
        sdk.UF_SetReceiveDataPacketCallback(receiveDataPacketCallback);
        sdk.UF_SetSendRawDataCallback(sendRawDataCallback);
        sdk.UF_SetReceiveRawDataCallback(receiveRawDataCallback);

        UF_RET_CODE ret = null;

        UFModuleInfo a = new UFModuleInfo();


        Log.d(TAG, String.format("Type : %s, version : %s, sensorType : %s", a.type(), a.version(), a.sensorType()));

        sdk.UF_GetModuleInfo(a);

        Log.d(TAG, String.format("Type : %s, version : %s, sensorType : %s", a.type(), a.version(), a.sensorType()));

        String moduleString = sdk.UF_GetModuleString(a.type(), a.version(), a.sensorType());
        Log.d(TAG, "Module String : " + moduleString);

    }

    private void Test_Calibrate_and_Reset() {
        final String TAG = "TEST_CALIBRATE_AND_RESET";

        // UF_Reconnect
        sdk.UF_Reconnect();

        // Callback test
        sdk.UF_SetSendPacketCallback(sendPacketCallback);
        sdk.UF_SetReceivePacketCallback(receivePacketCallback);
        sdk.UF_SetSendDataPacketCallback(sendDataPacketCallback);
        sdk.UF_SetReceiveDataPacketCallback(receiveDataPacketCallback);
        sdk.UF_SetSendRawDataCallback(sendRawDataCallback);
        sdk.UF_SetReceiveRawDataCallback(receiveRawDataCallback);

        UF_RET_CODE ret = null;

        ret = sdk.UF_CalibrateSensor();
        Log.d(TAG, "Calibrate Sensor : " + ret.toString());

        ret = sdk.UF_Reset();
        Log.d(TAG, "Reset Module : " + ret.toString());
    }

    private void Test_Lock_Unlock_Module() {
        final String TAG = "TEST_LOCK_UNLOCK_MODULE";

        // UF_Reconnect
        sdk.UF_Reconnect();

        // Callback test
        sdk.UF_SetSendPacketCallback(sendPacketCallback);
        sdk.UF_SetReceivePacketCallback(receivePacketCallback);
        sdk.UF_SetSendDataPacketCallback(sendDataPacketCallback);
        sdk.UF_SetReceiveDataPacketCallback(receiveDataPacketCallback);
        sdk.UF_SetSendRawDataCallback(sendRawDataCallback);
        sdk.UF_SetReceiveRawDataCallback(receiveRawDataCallback);

        UF_RET_CODE ret = null;

        ret = sdk.UF_Lock();
        Log.d(TAG, "Lock Module : " + ret.toString());

        byte[] password = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        ret = sdk.UF_Unlock(password);
        Log.d(TAG, "Unlock Module : " + ret.toString());

        byte[] newPassword = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};

        ret = sdk.UF_ChangePassword(newPassword, password);
        Log.d(TAG, "Change Password : " + ret.toString());

        ret = sdk.UF_Lock();
        Log.d(TAG, "Lock Module : " + ret.toString());

        ret = sdk.UF_Unlock(password);
        Log.d(TAG, "[Fail] Unlock Module : " + ret.toString());

        ret = sdk.UF_Unlock(newPassword);
        Log.d(TAG, "Unlock Module : " + ret.toString());

        // roll back
        ret = sdk.UF_ChangePassword(password, newPassword);
        Log.d(TAG, "[Roll back] Change Password : " + ret.toString());

        // Power off
        ret = sdk.UF_PowerOff();
        Log.d(TAG, "Power off : " + ret.toString());

    }

    private void Test_Template_Management_Part1() {
        final String TAG = "TEMPLATE_MANAGEMENT_1";

        // UF_Reconnect
        sdk.UF_Reconnect();

        // Callback test
        sdk.UF_SetSendPacketCallback(sendPacketCallback);
        sdk.UF_SetReceivePacketCallback(receivePacketCallback);
        sdk.UF_SetSendDataPacketCallback(sendDataPacketCallback);
        sdk.UF_SetReceiveDataPacketCallback(receiveDataPacketCallback);
        sdk.UF_SetSendRawDataCallback(sendRawDataCallback);
        sdk.UF_SetReceiveRawDataCallback(receiveRawDataCallback);
        sdk.UF_SetUserInfoCallback(userInfoCallback);
        sdk.UF_SetScanCallback(scanCallback);

        UF_RET_CODE ret = null;

        int[] numberOfTemplate = new int[1];
        ret = sdk.UF_GetNumOfTemplate(numberOfTemplate);
        Log.d(TAG, "Get Number of Template : " + ret.toString());
        Log.d(TAG, String.format("Number of Templates  %d", numberOfTemplate[0]));

        int[] maxNumberOfTemplate = new int[1];
        ret = sdk.UF_GetMaxNumOfTemplate(maxNumberOfTemplate);
        Log.d(TAG, "Get Max Number of Template : " + ret.toString());
        Log.d(TAG, String.format("Max Number of Templates  %d", maxNumberOfTemplate[0]));

        UFUserInfo[] userInfo = new UFUserInfo[10];
        for (int i = 0; i < 10; i++)
            userInfo[i] = new UFUserInfo();


        int[] numOfUser = new int[1];
        int[] numOfTemplates = new int[1];
        ret = sdk.UF_GetAllUserInfo(userInfo, numOfUser, numOfTemplates);
        Log.d(TAG, "Get All User Info : " + ret.toString());
        Log.d(TAG, String.format("Number of User : %d, Number of Templates : %d", numOfUser[0], numOfTemplates[0]));
        for (int i = 0; i < numOfUser[0]; i++)
            Log.d(TAG, String.format("User ID : %d , Num of Template : %d , Admin Level : %X", userInfo[i].userID(), userInfo[i].numOfTemplate(), userInfo[i].adminLevel()));

        numOfUser[0] = 0;
        numOfTemplates[0] = 0;

        UFUserInfoEx[] userInfoEx = new UFUserInfoEx[10];
        for (int i = 0; i < 10; i++)
            userInfoEx[i] = new UFUserInfoEx();
        ret = sdk.UF_GetAllUserInfoEx(userInfoEx, numOfUser, numOfTemplates);
        Log.d(TAG, "Get All User Info Ex : " + ret.toString());
        Log.d(TAG, String.format("Number of User : %d, Number of Templates : %d", numOfUser[0], numOfTemplates[0]));
        for (int i = 0; i < numOfUser[0]; i++) {
            for (int j = 0; j < userInfoEx[i].numOfTemplate(); j++) {
                Log.d(TAG, String.format("User ID : %d , Checksum : %X,  Num of Template : %d , Admin Level : %X", userInfoEx[i].userID(), userInfoEx[i].checkSum()[j], userInfoEx[i].numOfTemplate(), userInfoEx[i].adminLevel()));
            }
        }
        ret = sdk.UF_SetAdminLevel(1, UF_ADMIN_LEVEL.UF_ADMIN_LEVEL_ALL);
        Log.d(TAG, "Set Admin Level : " + ret.toString());

        UF_ADMIN_LEVEL[] adminLevel = new UF_ADMIN_LEVEL[1];
        ret = sdk.UF_GetAdminLevel(1, adminLevel);
        Log.d(TAG, "Get Admin Level : " + ret.toString());
        Log.d(TAG, "Admin Level : " + adminLevel[0].toString());

        ret = sdk.UF_SetSecurityLevel(1, UF_USER_SECURITY_LEVEL.UF_USER_SECURITY_1_TO_1000);
        Log.d(TAG, "Set User Security Level : " + ret.toString());

        UF_USER_SECURITY_LEVEL[] userSecurityLevel = new UF_USER_SECURITY_LEVEL[1];
        ret = sdk.UF_GetSecurityLevel(1, userSecurityLevel);
        Log.d(TAG, "Get User Security Level : " + ret.toString());
        Log.d(TAG, "User Security Level : " + userSecurityLevel[0].toString());

        ret = sdk.UF_ClearAllAdminLevel();
        Log.d(TAG, "Clear All Admin Level : " + ret.toString());

        numOfTemplates[0] = 0;
        ret = sdk.UF_CheckTemplate(1, numOfTemplates);
        Log.d(TAG, "Check Template : " + ret.toString());
        Log.d(TAG, String.format("Num of templates : %d", numOfTemplates[0]));

        byte[] templateData = new byte[384 * numOfTemplates[0]];
        ret = sdk.UF_ReadTemplate(1, numberOfTemplate, templateData);
        Log.d(TAG, "Read Template : " + ret.toString());

        String strTemplate = byteArrayToHex(templateData);
        Log.d(TAG, strTemplate);

        byte[] oneTemplateData = new byte[384];
        ret = sdk.UF_ReadOneTemplate(1, 0, oneTemplateData);
        Log.d(TAG, "Read One Template : " + ret.toString());

        String strOneTemplate = byteArrayToHex(oneTemplateData);
        Log.d(TAG, strOneTemplate);


        Arrays.fill(oneTemplateData, (byte) 0);
        int[] templateSize = new int[1];
        int[] imageQuality = new int[1];
        ret = sdk.UF_ScanTemplate(oneTemplateData, templateSize, imageQuality);
        Log.d(TAG, "Scan Template : " + ret.toString());

        strOneTemplate = byteArrayToHex(oneTemplateData);
        Log.d(TAG, String.format("Template Size : %d, Image Quality : %d ", templateSize[0], imageQuality[0]) + "TemplateData : " + strOneTemplate);

        ret = sdk.UF_FixProvisionalTemplate();
        Log.d(TAG, "Fix Provisional Template : " + ret.toString());

    }

    private void Test_Template_Management_Part2() {
        final String TAG = "TEMPLATE_MANAGEMENT_2";

        // UF_Reconnect
        sdk.UF_Reconnect();

        // Callback test
        sdk.UF_SetSendPacketCallback(sendPacketCallback);
        sdk.UF_SetReceivePacketCallback(receivePacketCallback);
        sdk.UF_SetSendDataPacketCallback(sendDataPacketCallback);
        sdk.UF_SetReceiveDataPacketCallback(receiveDataPacketCallback);
        sdk.UF_SetSendRawDataCallback(sendRawDataCallback);
        sdk.UF_SetReceiveRawDataCallback(receiveRawDataCallback);
        sdk.UF_SetScanCallback(scanCallback);


        UF_RET_CODE ret = null;

        UF_AUTH_TYPE[] authType = new UF_AUTH_TYPE[1];

        ret = sdk.UF_GetAuthType(1, authType);
        Log.d(TAG, "Get Auth Type : " + ret.toString());

        if (ret == UF_RET_CODE.UF_RET_SUCCESS)
            Log.d(TAG, "Auth Type : " + authType[0].toString());

        ret = sdk.UF_SetAuthType(1, UF_AUTH_TYPE.UF_AUTH_FINGERPRINT);
        Log.d(TAG, "Set Auth Type : " + ret.toString());

        ret = sdk.UF_ResetAllAuthType();
        Log.d(TAG, "Reset All Auth Type : " + ret.toString());

        ret = sdk.UF_DeleteAllBlacklist();
        Log.d(TAG, "Delete All Blacklist : " + ret.toString());

        ret = sdk.UF_ClearAllEntranceLimit();
        Log.d(TAG, "Clear All Entrance Limit : " + ret.toString());

//        final String filename = "/sdcard/templateDB.dat";
//        ret = sdk.UF_SaveDB(filename);
//        Log.d(TAG, "Save DB : " + ret.toString());
//
//
//        ret = sdk.UF_DeleteAll();
//        Log.d(TAG, "Delete All templates : " + ret.toString());
//
//        ret = sdk.UF_LoadDB(filename);
//        Log.d(TAG, "Load DB : " + ret.toString());
//
        int[] numOfId = new int[1];
        int[] userID = new int[1];
        ret = sdk.UF_GetUserIDByAuthType(UF_AUTH_TYPE.UF_AUTH_FINGERPRINT, numOfId, userID);
        Log.d(TAG, "Get User ID by Auth Type : " + ret.toString());

        int[] numOfBlacklistedID = new int[1];
        ret = sdk.UF_AddBlacklist(1, numOfBlacklistedID);
        Log.d(TAG, "Add Black list : " + ret.toString());

        ret = sdk.UF_DeleteBlacklist(1, numOfBlacklistedID);
        Log.d(TAG, "Delete Black list : " + ret.toString());

        ret = sdk.UF_GetBlacklist(numOfBlacklistedID, userID);
        Log.d(TAG, "Get Black list : " + ret.toString());

        ret = sdk.UF_SetEntranceLimit(1, 10);
        Log.d(TAG, "Set Entrance Limit : " + ret.toString());

        int entranceLimit[] = new int[1];
        int entranceCount[] = new int[1];
        ret = sdk.UF_GetEntranceLimit(1, entranceLimit, entranceCount);
        Log.d(TAG, "Get Entrance Limit : " + ret.toString());

        byte[] subID = new byte[1];
        ret = sdk.UF_Identify(userID, subID);
        Log.d(TAG, "Identify : " + ret.toString());
        Log.d(TAG, String.format("UserID : %d  SubID : %d", userID[0], subID[0]));
    }

    void Test_Image() {
        final String TAG = "IMAGE";
        // UF_Reconnect
        sdk.UF_Reconnect();

        // Callback test
        sdk.UF_SetSendPacketCallback(sendPacketCallback);
        sdk.UF_SetReceivePacketCallback(receivePacketCallback);
        sdk.UF_SetSendDataPacketCallback(sendDataPacketCallback);
        sdk.UF_SetReceiveDataPacketCallback(receiveDataPacketCallback);
        sdk.UF_SetSendRawDataCallback(sendRawDataCallback);
        sdk.UF_SetReceiveRawDataCallback(receiveRawDataCallback);
        sdk.UF_SetScanCallback(scanCallback);
        sdk.UF_SetIdentifyCallback(identifyCallback);

        UF_RET_CODE ret = null;

        // UF_Command
        int[] param = new int[]{0};
        int[] size = new int[]{0};
        byte[] flag = new byte[]{(byte) 0x79};

//        ret = sdk.UF_Command((byte)0x04, param, size, flag);
//        Log.d("UF_Command", ret.toString());

//        ret = sdk.UF_CommandEx((byte) 0x05, param, size, flag, msgCallback);
//        Log.d("UF_CommandEx", ret.toString());
//        Log.d("UF_CommandEx", String.format("%X %X %X", param[0], size[0], flag[0]));


        UFImage image = new UFImage();

        sdk.UF_SetSysParameter(UF_SYS_PARAM.UF_SYS_IMAGE_FORMAT, UF_IMAGE_TYPE.UF_GRAY_IMAGE.getValue());
        ret = sdk.UF_ScanImage(image);
        Log.d(TAG, "Scan Image : " + ret.toString());

//        ret = sdk.UF_ReadImage(image);
//        Log.d(TAG, "Read Image : " + ret.toString());

        Bitmap bmp = image.getBitmap();

//        ret = sdk.UF_SaveImage("/sdcard/fpimage.bmp", image);
//        Log.d(TAG, "Save Image : " + ret.toString());
//
//        UFImage loadedImage = new UFImage();
//        ret = sdk.UF_LoadImage("/sdcard/fpimage.bmp", loadedImage);
//        Log.d(TAG, "Load Image : " + ret.toString());
//
//
//        int []userID = new int[1];
//        byte [] subID = new byte[1];
//        ret = sdk.UF_Identify(userID, subID );
//
//        Bitmap bmp = loadedImage.getBitmap();
//        Log.d(TAG, "Load Image : " + ret.toString() + String.format(" userID :%d, subID : %d", userID[0], subID[0]));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bmp);
            }
        });
    }

    void Test_Identify() {
        final String TAG = "IDENTIFY";
        // UF_Reconnect
        sdk.UF_Reconnect();

        // Callback test
        sdk.UF_SetSendPacketCallback(sendPacketCallback);
        sdk.UF_SetReceivePacketCallback(receivePacketCallback);
        sdk.UF_SetSendDataPacketCallback(sendDataPacketCallback);
        sdk.UF_SetReceiveDataPacketCallback(receiveDataPacketCallback);
        sdk.UF_SetSendRawDataCallback(sendRawDataCallback);
        sdk.UF_SetReceiveRawDataCallback(receiveRawDataCallback);
        sdk.UF_SetScanCallback(scanCallback);

        UF_RET_CODE ret = null;

        int[] numOfTemplate = new int[1];
        byte[] templateData = new byte[3840];
        ret = sdk.UF_ReadTemplate(1, numOfTemplate, templateData);
        Log.d(TAG, "Test_Identify: Read Template : " + ret.toString());

        int[] userID = new int[1];
        byte[] subID = new byte[1];
        ret = sdk.UF_IdentifyTemplate(384, templateData, userID, subID);
        Log.d(TAG, "Test_Identify: IdentifyTemplate : " + ret.toString());
    }

    void Test_Enroll() {
        final String TAG = "ENROLL";
        // UF_Reconnect
        sdk.UF_Reconnect();

        // Callback test
//        sdk.UF_SetSendPacketCallback(sendPacketCallback);
//        sdk.UF_SetReceivePacketCallback(receivePacketCallback);
//        sdk.UF_SetSendDataPacketCallback(sendDataPacketCallback);
//        sdk.UF_SetReceiveDataPacketCallback(receiveDataPacketCallback);
        sdk.UF_SetSendRawDataCallback(sendRawDataCallback);
        sdk.UF_SetReceiveRawDataCallback(receiveRawDataCallback);
        sdk.UF_SetScanCallback(scanCallback);
        sdk.UF_SetEnrollCallback(enrollCallback);
        sdk.UF_SetDeleteCallback(deleteCallback);

        UF_RET_CODE ret = null;

        int userID = 0;
        int[] enrollID = new int[1];
        int[] imageQuality = new int[1];
        int[] numOfTemplate = new int[1];
        byte[] templateData = new byte[3840];
        int[] templateSize = new int[1];
        sdk.UF_ScanTemplate(templateData, templateSize, imageQuality);
//        sdk.UF_SetGenericCommandTimeout(5000);
//        sdk.UF_SetAdminLevel(10, UF_ADMIN_LEVEL.UF_ADMIN_LEVEL_ALL);
//
//        ret = sdk.UF_Enroll(userID, UF_ENROLL_OPTION.UF_ENROLL_AUTO_ID, enrollID, imageQuality);
//        Log.d(TAG, "Test_Enroll: Enroll : " + ret.toString() + String.format(" enrollID : %d, imageQaulity : %d", enrollID[0], imageQuality[0]));
//
//        ret =  sdk.UF_EnrollAfterVerification(10, UF_ENROLL_OPTION.UF_ENROLL_AUTO_ID, imageQuality, imageQuality);
//        if(ret == UF_RET_CODE.UF_RET_SUCCESS)
//            Log.d(TAG, "Test_Enroll: Enroll After Verification : " + ret.toString() + String.format(" enrollID : %d, imageQaulity : %d", enrollID[0], imageQuality[0]));

//        ret = sdk.UF_EnrollContinue(0, enrollID, imageQuality);
//        if(ret == UF_RET_CODE.UF_RET_SUCCESS)
//            Log.d(TAG, "Test_Enroll: Enroll Continue : " + ret.toString() + String.format(" enrollID : %d, imageQaulity : %d", enrollID[0], imageQuality[0]));

//        ret = sdk.UF_ReadTemplate(1, numOfTemplate, templateData);
//        Log.d(TAG, "Test_Enroll: Read Template :" + ret.toString());

        ret = sdk.UF_EnrollTemplate(0, UF_ENROLL_OPTION.UF_ENROLL_AUTO_ID, 384, templateData, enrollID);
        Log.d(TAG, "Test_Enroll: Enroll Template : " + ret.toString() + String.format(" enrollID : %d", enrollID[0]));

//        ret = sdk.UF_EnrollMultipleTemplates(0, UF_ENROLL_OPTION.UF_ENROLL_AUTO_ID, 1, 384, templateData, enrollID);
//        Log.d(TAG, "Test_Enroll: Enroll Multiple Template : " + ret.toString() + String.format(" enrollID : %d", enrollID[0]));
//
//        ret = sdk.UF_EnrollMultipleTemplatesEx(0, UF_ENROLL_OPTION.UF_ENROLL_AUTO_ID, 1, 1, 384, templateData, enrollID);
//        Log.d(TAG, "Test_Enroll: Enroll Multiple Template : " + ret.toString() + String.format(" enrollID : %d", enrollID[0]));

        UFImage image = new UFImage();

        ret = sdk.UF_LoadImage("/sdcard/fpimage.bmp", image);
        ret = sdk.UF_EnrollImage(0, UF_ENROLL_OPTION.UF_ENROLL_AUTO_ID, image.imgLen(), image.buffer(), enrollID, imageQuality);
        Log.d(TAG, "Test_Enroll: Enroll Image " + ret.toString() + String.format("userID : %d , image Quality : %d", enrollID[0], imageQuality[0]));

        image = null;
    }

    void Test_Verify() {
        final String TAG = "VERIFY";
        // UF_Reconnect
        sdk.UF_Reconnect();

        // Callback test
        sdk.UF_SetSendPacketCallback(sendPacketCallback);
        sdk.UF_SetReceivePacketCallback(receivePacketCallback);
        sdk.UF_SetSendDataPacketCallback(sendDataPacketCallback);
        sdk.UF_SetReceiveDataPacketCallback(receiveDataPacketCallback);
        sdk.UF_SetSendRawDataCallback(sendRawDataCallback);
        sdk.UF_SetReceiveRawDataCallback(receiveRawDataCallback);
        sdk.UF_SetScanCallback(scanCallback);
        sdk.UF_SetVerifyCallback(verifyCallback);

        UF_RET_CODE ret = null;

        int[] numOfTemplate = new int[1];
        byte[] templateData = new byte[3840];

        ret = sdk.UF_ReadTemplate(10, numOfTemplate, templateData);

        byte[] subID = new byte[1];

        ret = sdk.UF_Verify(10, subID);
        Log.d(TAG, "Test_Verify: Verify : " + ret.toString() + String.format(" SubID : %d", subID[0]));

        ret = sdk.UF_VerifyHostTemplate(1, 384, templateData);
        Log.d(TAG, "Test_Verify: Verify by Host: " + ret.toString());

        ret = sdk.UF_VerifyTemplate(384, templateData, 10, subID);
        Log.d(TAG, "Test_Verify: Verify by Template: " + ret.toString());

        UFImage image = new UFImage();
        ret = sdk.UF_ScanImage(image);
        ret = sdk.UF_VerifyImage(image.imgLen(), image.buffer(), 10, subID);
        Log.d(TAG, "Test_Verify: Verify by Image: " + ret.toString() + ret.toString() + String.format(" SubID : %d", subID[0]));


    }

    void Test_Delete() {
        final String TAG = "DELETE";
        // UF_Reconnect
        sdk.UF_Reconnect();

        // Callback test
        ////sdk.UF_SetSendPacketCallback(sendPacketCallback);
        //sdk.UF_SetReceivePacketCallback(receivePacketCallback);
        sdk.UF_SetSendDataPacketCallback(sendDataPacketCallback);
        sdk.UF_SetReceiveDataPacketCallback(receiveDataPacketCallback);
        sdk.UF_SetSendRawDataCallback(sendRawDataCallback);
        sdk.UF_SetReceiveRawDataCallback(receiveRawDataCallback);
        sdk.UF_SetScanCallback(scanCallback);
        sdk.UF_SetEnrollCallback(enrollCallback);
        sdk.UF_SetDeleteCallback(deleteCallback);

        UF_RET_CODE ret = null;

        int[] numOfTemplate = new int[1];
        byte[] templateData = new byte[3840];

        ret = sdk.UF_DeleteAll();
        Log.d(TAG, "Test_Delete: Delete All : " + ret.toString());

        int[] templateSize = new int[1];
        int[] imageQuality = new int[1];
        int[] enrollID = new int[1];

        ret = sdk.UF_ScanTemplate(templateData, templateSize, imageQuality);
        Log.d(TAG, "Test_Delete: Scan Template : " + ret.toString());

        for (int i = 0; i < 100; i++) {
            ret = sdk.UF_EnrollTemplate(0, UF_ENROLL_OPTION.UF_ENROLL_AUTO_ID, templateSize[0], templateData, enrollID);
            Log.d(TAG, "Test_Delete: Enroll Template : " + ret.toString() + String.format("EnrollID : %d", enrollID[0]));
        }

        ret = sdk.UF_Delete(1);
        Log.d(TAG, "Test_Delete: Delete Template : " + ret.toString());

        ret = sdk.UF_SetAdminLevel(10, UF_ADMIN_LEVEL.UF_ADMIN_LEVEL_ALL);
        ret = sdk.UF_DeleteOneTemplate(2, 1);
        Log.d(TAG, "Test_Delete: Delete One Template : " + ret.toString());

        int[] deletedID = new int[1];
        ret = sdk.UF_DeleteMultipleTemplates(20, 30, deletedID);
        Log.d(TAG, "Test_Delete: Delete Multiple Template : " + ret.toString() + String.format(" Number of Deleted ID  : %d", deletedID[0]));

        sdk.UF_DeleteAllAfterVerification();
    }

    void Test_FileSystem() throws InterruptedException {
        final String TAG = "FILESYSTEM";
        // UF_Reconnect
        sdk.UF_Reconnect();

        // Callback test
        sdk.UF_SetSendPacketCallback(sendPacketCallback);
        sdk.UF_SetReceivePacketCallback(receivePacketCallback);
        sdk.UF_SetSendDataPacketCallback(sendDataPacketCallback);
        sdk.UF_SetReceiveDataPacketCallback(receiveDataPacketCallback);

        UF_RET_CODE ret = null;

//        ret = sdk.UF_FormatUserDatabase();
//        Log.d(TAG, "Test_FileSystem: Format User Database : " + ret.toString());


        ret = sdk.UF_ResetSystemConfiguration();
        Log.d(TAG, "Test_FileSystem: ResetSystemConfituration : " + ret.toString());


    }

    void Test_SearchModule() {
        final String TAG = "SEARCH_MODULE";
        // UF_Reconnect
        sdk.UF_Reconnect();

        // Callback test
        sdk.UF_SetSendPacketCallback(sendPacketCallback);
        sdk.UF_SetReceivePacketCallback(receivePacketCallback);
        sdk.UF_SetSendDataPacketCallback(sendDataPacketCallback);
        sdk.UF_SetReceiveDataPacketCallback(receiveDataPacketCallback);

        UF_RET_CODE ret = null;

        String port = sdk.UF_GetDevicePort();
        int[] baudrate = new int[1];
        boolean[] asciiMode = new boolean[1];
        UF_PROTOCOL[] protocol = new UF_PROTOCOL[1];
        int[] moduleID = new int[1];

        sdk.UF_SetSysParameter(UF_SYS_PARAM.UF_SYS_PROTOCOL_INTERFACE, UF_PROTOCOL.UF_NETWORK_PROTOCOL.getValue());


        ret = sdk.UF_SearchModule(port, baudrate, asciiMode, protocol, moduleID, searchModuleCallback);
        Log.d(TAG, "Test_SearchModule: Search Module : " + ret.toString() + String.format("baudrate : %d,  asciiMode : %s,  moduleID : %d", baudrate[0], Boolean.toString(asciiMode[0]), moduleID[0]));
        Log.d(TAG, "Test_SearchModule: Search Module : " + protocol[0].toString());


        ret = sdk.UF_SearchModuleID(moduleID);
        Log.d(TAG, "Test_SearchModule: Search Module ID : " + String.format("module ID : %d", moduleID[0]));


        short[] foundModuleID = new short[10];
        int[] numOfID = new int[10];
        short[] moduleID_short = new short[10];
        ret = sdk.UF_SearchModuleIDEx(foundModuleID, 0, moduleID_short, numOfID);
        Log.d(TAG, "Test_SearchModule: Search Module ID Ex :" + ret.toString());

    }

    void Test_Upgrade() {
        final String TAG = "UPGRADE";
        // UF_Reconnect
        sdk.UF_Reconnect();

        // Callback test
        sdk.UF_SetSendPacketCallback(sendPacketCallback);
        sdk.UF_SetReceivePacketCallback(receivePacketCallback);
        sdk.UF_SetSendDataPacketCallback(sendDataPacketCallback);
        sdk.UF_SetReceiveDataPacketCallback(receiveDataPacketCallback);

        UF_RET_CODE ret = null;

        ret = sdk.UF_Upgrade("/sdcard/SFMFW_SFMSLIM_SLIM_S34A_19071814_680C4935.bin", 4096);
        Log.d(TAG, "Test_Upgrade: " + ret.toString());

    }

    void Test_WSQ() {
        final String TAG = "WSQ";
        // UF_Reconnect
        sdk.UF_Reconnect();

        // Callback test
        sdk.UF_SetSendPacketCallback(sendPacketCallback);
        sdk.UF_SetReceivePacketCallback(receivePacketCallback);
        sdk.UF_SetSendDataPacketCallback(sendDataPacketCallback);
        sdk.UF_SetReceiveDataPacketCallback(receiveDataPacketCallback);
        sdk.UF_SetScanCallback(scanCallback);

        UF_RET_CODE ret = null;

        UFImage image = new UFImage();
        ret = sdk.UF_SetSysParameter(UF_SYS_PARAM.UF_SYS_IMAGE_FORMAT, UF_IMAGE_TYPE.UF_WSQ_IMAGE.getValue());
        Log.d(TAG, "Test_WSQ: Set System Parameter :  " + ret.toString());

        sdk.UF_Save();

//        ret = sdk.UF_ScanImageEx(image, UF_IMAGE_TYPE.UF_WSQ_MQ_IMAGE, 225);

        ret = sdk.UF_ReadImageEx(image, UF_IMAGE_TYPE.UF_WSQ_MQ_IMAGE, 225);
        Log.d(TAG, "Test_WSQ: " + ret.toString());


        int[] ow = new int[1];
        int[] oh = new int[1];
        int[] od = new int[1];
        int[] oppi = new int[1];
        int[] lossyflag = new int[1];


        ret = sdk.UF_WSQ_Decode(image.rawData(), ow, oh, od, oppi, lossyflag, image.buffer(), image.imgLen());

        Log.d(TAG, "Test_WSQ: " + ret.toString() + String.format(" %02X  %02X", image.buffer()[0], image.buffer()[1]));


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(image.getWSQImage());
            }
        });


    }

    private class SFMTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                Test_WSQ();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private void Test_NDK() {

        sdk.NDKCallback_Test();
    }

    private SFMTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = (TextView) findViewById(R.id.textView1);
        editText = (EditText) findViewById(R.id.editText1);
        imageView = (ImageView) findViewById(R.id.imageView);
        sdk = new SFM_SDK_ANDROID(this, mHandler, mUsbReceiver);

        Button sendButton = (Button) findViewById(R.id.buttonSend);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!editText.getText().toString().equals("")) {

                    task = new SFMTask();
                    task.execute();

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Test_Image();
//                        }
//                    }).start();

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        sdk.resumeService();

    }

    @Override
    public void onPause() {
        super.onPause();
        sdk.pauseService();
    }
}
