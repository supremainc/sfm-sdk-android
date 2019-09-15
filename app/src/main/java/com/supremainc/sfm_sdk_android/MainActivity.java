package com.supremainc.sfm_sdk_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.supremainc.sfm_sdk.MessageHandler;
import com.supremainc.sfm_sdk.SFM_SDK_ANDROID;
import com.supremainc.sfm_sdk.UF_MODULE_INFO;
import com.supremainc.sfm_sdk.UF_SYS_PARAM;
import com.supremainc.sfm_sdk.UsbService;
import com.supremainc.sfm_sdk.enumeration.UF_PROTOCOL;
import com.supremainc.sfm_sdk.enumeration.UF_RET_CODE;

import java.util.Arrays;
//import com.supremainc.sfm_sdk.MessageHandler;
//import com.supremainc.sfm_sdk.SFM_SDK_ANDROID;
//import com.supremainc.sfm_sdk.UsbService;

//import com.supremainc.sfm_sdk.MessageHandler;
//import com.supremainc.sfm_sdk.SFM_SDK_ANDROID;
//import com.supremainc.sfm_sdk.UsbService;


public class MainActivity extends AppCompatActivity {


    private TextView display;
    private EditText editText;

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

    private final MessageHandler mHandler = new MessageHandler(this)
    {
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
        for(final byte b: a)
            sb.append(String.format("%02X ", b&0xff));
        return sb.toString();
    }

    SFM_SDK_ANDROID.SendPacketCallback sendPacketCallback = new SFM_SDK_ANDROID.SendPacketCallback() {
        @Override
        public void callback(byte[] data) {
            display.post(new Runnable() {
                @Override
                public void run() {
                    final String str = "[SEND]" + byteArrayToHex(data) + "\n";
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
                    final String str = "[RECV]" + byteArrayToHex(data) + "\n";
                    display.append(str);
                }
            });
        }
    };

    SFM_SDK_ANDROID.SendDataPacketCallback sendDataPacketCallback = new SFM_SDK_ANDROID.SendDataPacketCallback() {
        @Override
        public void callback(int index, int numOfPacket) {

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

        UF_MODULE_INFO a = new UF_MODULE_INFO();


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = (TextView) findViewById(R.id.textView1);
        editText = (EditText) findViewById(R.id.editText1);
        sdk = new SFM_SDK_ANDROID(this, mHandler, mUsbReceiver);
        Button sendButton = (Button) findViewById(R.id.buttonSend);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!editText.getText().toString().equals("")) {

                    Test_Lock_Unlock_Module();
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