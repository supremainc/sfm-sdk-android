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
import com.supremainc.sfm_sdk.UF_SYS_PARAM;
import com.supremainc.sfm_sdk.UsbService;
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
                    UF_RET_CODE result = sdk.UF_InitCommPort(115200, true);
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

                    Test_Basic_Packet_Interface();
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