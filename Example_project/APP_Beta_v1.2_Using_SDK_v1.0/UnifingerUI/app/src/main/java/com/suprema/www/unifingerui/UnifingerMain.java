package com.suprema.www.unifingerui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Iterator;


public class UnifingerMain extends Activity {
    SerialConnect m_serialConnect;
    //CommandResponse m_commandResponse;

    boolean connected = false;

    Bitmap bitmap;

    ImageView fingerImg;

    int UserID;
    TextView tx;
    EditText IDedit;

    CommandCall m_sensorControl;

    /*local variables*/
    int baudRate =115200; /*baud rate*/
    byte stopBit =1; /*1:1stop bits, 2:2 stop bits*/
    byte dataBit =8; /*8:8bit, 7: 7bit*/
    byte parity =0;  /* 0: none, 1: odd, 2: even, 3: mark, 4: space*/
    boolean flowControl_XON_XOFF_Flag=false; /*0:none, 1: flow control(CTS,RTS)*/
    int m_nImg_width = 272;
    int m_nImg_height = 320;


    @Override
    public void onStart() {
        super.onStart();
        m_serialConnect.createDeviceList();
    }

    @Override
    public void onStop()
    {
        m_serialConnect.disconnectFunction();
        super.onStop();
    }
    @Override
    public void onResume() {
        super.onResume();
        m_serialConnect.SetDevCount(0);
        m_serialConnect.createDeviceList();
        if(m_serialConnect.GetDevCount() > 0)
        {
            m_serialConnect.connectFunction();
            m_serialConnect.SetConfig(baudRate, dataBit, stopBit, parity, flowControl_XON_XOFF_Flag);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unifinger_main);

        bitmap = Bitmap.createBitmap(m_nImg_width, m_nImg_height, Bitmap.Config.ALPHA_8);

        IDedit=(EditText)findViewById(R.id.UserID);
        IDedit.setOnKeyListener(mEditListener);
        IDedit.setOnClickListener(mBtnListener);
        tx = (TextView)findViewById(R.id.test);

        m_serialConnect = new SerialConnect(this,CONNECTION_TYPE.LEGACY_USB);
        m_sensorControl = new CommandCall(m_serialConnect,SetUserID_Edit);




        //m_commandResponse = new CommandResponse(this);
        //m_sensorControl.SetM(this);

        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(0xFF3189D1));


        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.setPriority(500);
        this.registerReceiver(mUsbReceiver, filter);

        UsbManager manager =(UsbManager) getSystemService(Context.USB_SERVICE);

        HashMap<String,UsbDevice> deviceList = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            UsbDevice device = deviceIterator.next();
            if(device.getDeviceName().contains("/dev/bus/usb/001/") )
            {
                tx.setText("Sensor Connected ");
                connected=true;
                if(m_serialConnect.GetConnectionType() == CONNECTION_TYPE.LEGACY_USB)
                {
                    m_serialConnect.connectFunction();
                }
                else if(m_serialConnect.GetConnectionType() == CONNECTION_TYPE.FTDI) {
                }
            }
        }

        if(connected==false)
        {
            tx.setText("Sensor disconnected ");
        }

        ImageButton EnrollBtn=(ImageButton)findViewById(R.id.EnrollBtn);
        EnrollBtn.setOnClickListener(mBtnListener);
        ImageButton IdentifyBtn=(ImageButton)findViewById(R.id.IdentifyBtn);
        IdentifyBtn.setOnClickListener(mBtnListener);
        ImageButton VerifyBtn=(ImageButton)findViewById(R.id.VerifyBtn);
        VerifyBtn.setOnClickListener(mBtnListener);

        fingerImg=(ImageView)findViewById(R.id.fingerImage);
        fingerImg.setOnClickListener(mBtnListener);

    }

    private View.OnKeyListener mEditListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event)
        {
            if(keyCode==KeyEvent.KEYCODE_ENTER)
            {
                IDedit.clearFocus();
                IDedit.setCursorVisible(true);
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(IDedit.getWindowToken(),0);

            }
            return false;
        }
    };
    byte [] FPImage = new byte[m_nImg_width * m_nImg_height];
    private View.OnClickListener mBtnListener = new View.OnClickListener(){
        public void onClick(View v){
            if(connected==true) {
                switch (v.getId()) {
                    case R.id.EnrollBtn:
                        try {
                            UserID = Integer.parseInt(IDedit.getText().toString());
                            int result= m_sensorControl.UF_Enroll(UserID);
                            if(result==0)
                                IDedit.setText("Enroll Success!!");
                            else
                                IDedit.setText("Enroll Fail !!");
                        } catch (Exception e) {
                            IDedit.setText("Only input the number");
                        }
                        break;

                    case R.id.IdentifyBtn:
                        m_sensorControl.UF_Identify();
                        break;

                    case R.id.VerifyBtn:
                        try {
                            UserID = Integer.parseInt(IDedit.getText().toString());
                            int result = m_sensorControl.UF_Verify(UserID);
                            if(result==0)
                                IDedit.setText("Success!!");
                            else if ( result ==-1)
                                IDedit.setText("Match Fail !!");
                        } catch (Exception e) {
                            IDedit.setText("Only input the number");
                        }
                        break;

                    case R.id.fingerImage:
                        m_sensorControl.UF_SetDefaultPacketSIze(PACKET_INFO.UF_DEFAULT_DATA_PACKET_SIZE);
                        FPImage = m_sensorControl.UF_ScanImage(m_nImg_width,m_nImg_height);
                        InverseImage(FPImage,m_nImg_width,m_nImg_height);
                        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(FPImage));
                        fingerImg.setImageBitmap(bitmap);
                        fingerImg.setBackgroundColor(Color.rgb(255,255,255));
                        break;

                    case R.id.UserID:
                        IDedit.setText("");

                }

            }
        }
    };


    public void InverseImage(byte[] fingerImg, int width, int height )
    {
        for(int i=0; i< width * height; i++)
        {
            fingerImg[i] = (byte)(255 - fingerImg[i]);
        }
    }


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String TAG = "FragL";
            String action = intent.getAction();
            if(UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action))
            {
                Log.i(TAG, "DETACHED...");

                if (m_serialConnect != null)
                {
                    tx.setText("Sensor disconnected");
                    connected =false;
                    m_serialConnect.disconnectFunction();

                }
            }
            else if(UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action))
            {
                if(m_serialConnect.GetDevCount() <= 0)
                {
                    m_serialConnect.createDeviceList();
                }
                else
                {
                    tx.setText("Sensor Connected ");
                    connected = true;
                    if(m_serialConnect.GetConnectionType() == CONNECTION_TYPE.LEGACY_USB)
                    {
                        m_serialConnect.connectFunction();
                    }
                    else if(m_serialConnect.GetConnectionType() == CONNECTION_TYPE.FTDI) {
                    }
                }
            }
        }
    };


    public Handler SetUserID_Edit = new Handler() {
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            String strvalue = data.getString("Identify_Result");

            IDedit.setText(strvalue);
        }
    };




}
