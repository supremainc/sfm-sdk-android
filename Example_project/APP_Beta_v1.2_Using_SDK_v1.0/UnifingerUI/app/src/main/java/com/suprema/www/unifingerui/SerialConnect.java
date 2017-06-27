package com.suprema.www.unifingerui;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by jmlee on 2017-04-05.
 */
public class SerialConnect {
    D2xxManager m_ftdid2xx;
    public FT_Device m_ftDev = null;
    static Context m_DeviceUARTContext;
    int m_nDevCount = -1;
    int m_nCurrentIndex = -1;
    int m_nOpenIndex = 0;
    ExecutorService executor = Executors.newFixedThreadPool(1);
    public int m_nBaudRate; /*baud rate*/
    public byte m_byStopBit; /*1:1stop bits, 2:2 stop bits*/
    public byte m_byDataBit; /*8:8bit, 7: 7bit*/
    public byte m_byParity;  /* 0: none, 1: odd, 2: even, 3: mark, 4: space*/
    public boolean m_bFlowControl_XON_XOFF_Flag; /*0:none, 1: flow control(CTS,RTS)*/

    public byte [] tempBuffer;
    public int m_nRead_len=0;
    private int m_connectionType;
    private SerialPort serialPort = null;
    protected OutputStream mOutputStream;
    private InputStream mInputStream;


    public void SetConnectionType(int connectionType)
    {
        m_connectionType = connectionType;
    }
    public int GetConnectionType()
    {
        return m_connectionType;
    }




    public SerialConnect(Context DeviceUARTContext, int connectionType)
    {
        m_connectionType = connectionType;
        if(m_connectionType == CONNECTION_TYPE.FTDI) {
            m_DeviceUARTContext = DeviceUARTContext;
            try {
                m_ftdid2xx = D2xxManager.getInstance(DeviceUARTContext);
            } catch (D2xxManager.D2xxException ex) {
                ex.printStackTrace();
            }
        }

    }

    public int GetDevCount()
    {
        return m_nDevCount;
    }
    public void SetDevCount(int nDevCount)
    {
        m_nDevCount= nDevCount;
    }
    public void SetConfig( int nBaudRate, byte byStopBit, byte byDataBit, byte byParity, boolean bFlowControl_XON_XOFF_Flag)
    {
        m_nBaudRate = nBaudRate;
        m_byStopBit = byStopBit;
        m_byDataBit = byDataBit;
        m_byParity = byParity;
        m_bFlowControl_XON_XOFF_Flag = bFlowControl_XON_XOFF_Flag;

        InitConfig(m_nBaudRate, m_byStopBit, m_byDataBit, m_byParity, m_bFlowControl_XON_XOFF_Flag);
    }


    public void InitConfig(int nBaudRate, byte byStopBit, byte byDataBit, byte byParity, boolean bFlowControl_XON_XOFF_Flag)
    {
        if (m_ftDev.isOpen() == false) {
            Log.e("j2xx", "SetConfig: device not open");
            return;
        }

        m_ftDev.setBitMode((byte) 0, D2xxManager.FT_BITMODE_RESET);


        m_ftDev.setBaudRate(nBaudRate);

        switch (byDataBit) {
            case 7:
                byDataBit = D2xxManager.FT_DATA_BITS_7;
                break;
            case 8:
                byDataBit = D2xxManager.FT_DATA_BITS_8;
                break;
            default:
                byDataBit = D2xxManager.FT_DATA_BITS_8;
                break;
        }

        switch (byStopBit) {
            case 1:
                byStopBit = D2xxManager.FT_STOP_BITS_1;
                break;
            case 2:
                byStopBit = D2xxManager.FT_STOP_BITS_2;
                break;
            default:
                byStopBit = D2xxManager.FT_STOP_BITS_1;
                break;
        }

        switch (byParity) {
            case 0:
                byParity = D2xxManager.FT_PARITY_NONE;
                break;
            case 1:
                byParity = D2xxManager.FT_PARITY_ODD;
                break;
            case 2:
                byParity = D2xxManager.FT_PARITY_EVEN;
                break;
            case 3:
                byParity = D2xxManager.FT_PARITY_MARK;
                break;
            case 4:
                byParity = D2xxManager.FT_PARITY_SPACE;
                break;
            default:
                byParity = D2xxManager.FT_PARITY_NONE;
                break;
        }

        m_ftDev.setDataCharacteristics(byDataBit, byStopBit, byParity);

        short flowCtrlSetting;
        if (bFlowControl_XON_XOFF_Flag == true)
            flowCtrlSetting = D2xxManager.FT_FLOW_XON_XOFF;
        else
            flowCtrlSetting = D2xxManager.FT_FLOW_NONE;

        m_ftDev.setFlowControl(flowCtrlSetting, (byte) 0x0b, (byte) 0x0d);

        Toast.makeText(m_DeviceUARTContext, "Config done", Toast.LENGTH_SHORT).show();
    }

    public void createDeviceList()
    {
        if(m_connectionType == CONNECTION_TYPE.FTDI) {
            int tempDevCount = m_ftdid2xx.createDeviceInfoList(m_DeviceUARTContext);

            if (tempDevCount > 0) {
                if (m_nDevCount != tempDevCount) {
                    m_nDevCount = tempDevCount;
                }
            } else {
                m_nDevCount = -1;
                m_nCurrentIndex = -1;
            }
        }
    }
    public void connectFunction()
    {

        if(m_connectionType == CONNECTION_TYPE.LEGACY_USB) {
            try {
                serialPort = new SerialPort(new File("/dev/ttyACM0"), m_nBaudRate, 0);
                mOutputStream = serialPort.getOutputStream();
                mInputStream = serialPort.getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        else if (m_connectionType == CONNECTION_TYPE.FTDI) {

            createDeviceList();
            int tmpProtNumber = m_nOpenIndex + 1;

            if( m_nCurrentIndex != m_nOpenIndex )
            {
                if(null == m_ftDev)
                {
                    m_ftDev = m_ftdid2xx.openByIndex(m_DeviceUARTContext, m_nOpenIndex);
                    SetConfig(m_nBaudRate, m_byStopBit, m_byDataBit, m_byParity, m_bFlowControl_XON_XOFF_Flag);
                }
                else
                {
                    synchronized(m_ftDev)
                    {
                        m_ftDev = m_ftdid2xx.openByIndex(m_DeviceUARTContext, m_nOpenIndex);
                    }
                }
            }
            else
            {
                Toast.makeText(m_DeviceUARTContext, "Device port " + tmpProtNumber + " is already opened", Toast.LENGTH_LONG).show();
                return;
            }

            if(m_ftDev == null)
            {
                Toast.makeText(m_DeviceUARTContext,"open device port("+tmpProtNumber+") NG, ftDev == null", Toast.LENGTH_LONG).show();
                return;
            }

            if (true == m_ftDev.isOpen())
            {
                m_nCurrentIndex = m_nOpenIndex;
                Toast.makeText(m_DeviceUARTContext, "open device port(" + tmpProtNumber + ") OK", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(m_DeviceUARTContext, "open device port(" + tmpProtNumber + ") NG", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void disconnectFunction()
    {
        if(m_connectionType==CONNECTION_TYPE.FTDI) {
            m_nDevCount = -1;
            m_nCurrentIndex = -1;
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (m_ftDev != null) {
                synchronized (m_ftDev) {
                    if (true == m_ftDev.isOpen()) {
                        m_ftDev.close();
                    }
                }
            }
        }
    }

//    Callable<Integer> sendTask = new Callable<Integer>() {
//        @Override
//        public Integer call() throws Exception {
//            return m_ftDev.write(sendPacket, sendPacket.length);
//        }
//    };
//
//    byte[] sendPacket;

    public int SendPacket(String var)  {
        int returnSize =0;
        if(m_connectionType == CONNECTION_TYPE.FTDI) {
            if ( m_ftDev.isOpen() == false) {
                Log.e("j2xx", "SendMessage: device not open");
                return -1;
            }
            m_ftDev.setLatencyTimer((byte) 16);
        }

        byte[] sendPacket = new BigInteger( var,16).toByteArray();//writeData.getBytes();


        if(sendPacket.length==PACKET_INFO.UF_PACKET_LEN) {
            if(m_connectionType == CONNECTION_TYPE.FTDI) {
                returnSize=m_ftDev.write(sendPacket, sendPacket.length);
            }
            else if(m_connectionType == CONNECTION_TYPE.LEGACY_USB) {
                try {
                    mOutputStream.write(sendPacket);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                returnSize = PACKET_INFO.UF_PACKET_LEN;
            }
        }

        else
            Log.e("sendPacket", "Wrong sendPacket length");

        return returnSize;
    }


    Callable<Integer> readTask = new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
            if(m_connectionType == CONNECTION_TYPE.FTDI) {
                return m_ftDev.read(tempBuffer,m_nRead_len);
            }
            else if (m_connectionType == CONNECTION_TYPE.LEGACY_USB) {
                int retSize = 0;
                do {
                    retSize = mInputStream.read(tempBuffer);
                } while (retSize <= 0);
                return retSize;
            }

            return -1;
        }

    };

    public int ReadPacket(int size, byte[] read_buffer) throws InterruptedException, ExecutionException, TimeoutException {
        //m_ftDev.setLatencyTimer((byte)200);
        long lnStart =0, lnEnd =0;
        int nTime_out = 10000;
        int returnSize = 0;
        m_nRead_len = 0;

        lnStart = SystemClock.currentThreadTimeMillis();

        if(m_connectionType == CONNECTION_TYPE.FTDI) {
            do {
                m_nRead_len = m_ftDev.getQueueStatus();
                lnEnd = SystemClock.currentThreadTimeMillis();
            }while(m_nRead_len <=0 && ((lnEnd - lnStart)<nTime_out));
            if (m_nRead_len > size)
            m_nRead_len = size;
            else if (  m_nRead_len == 0)
            return -1;
            tempBuffer = new byte[m_nRead_len+3];
            Future<Integer> future = executor.submit(readTask);
            returnSize = future.get(3000, TimeUnit.MILLISECONDS);

            System.arraycopy(tempBuffer,0,read_buffer,0,m_nRead_len);
        }

        else if(m_connectionType == CONNECTION_TYPE.LEGACY_USB) {
            do {

                tempBuffer = new byte[size - returnSize];

                Future<Integer> future = executor.submit(readTask);
                m_nRead_len = future.get(10000, TimeUnit.MILLISECONDS);

                if (m_nRead_len > 0) {
                    System.arraycopy(tempBuffer, 0, read_buffer, returnSize, m_nRead_len);
                    returnSize += m_nRead_len;
                }

                lnEnd = SystemClock.currentThreadTimeMillis();
            } while (returnSize < size && ((lnEnd - lnStart) < nTime_out));
        }

        return returnSize;
    }

}
