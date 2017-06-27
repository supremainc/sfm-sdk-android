package com.suprema.www.unifingerui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by jmlee on 2017-04-05.
 */
public class CommandCall extends Command {
    public SerialConnect m_SerialConnect;
    private Handler m_MainEditHandler;

    public CommandCall( SerialConnect SerialConnect, Handler MainEditHandler)
    {
        m_MainEditHandler = MainEditHandler;
        m_SerialConnect = SerialConnect;

        SetCommandClassName("com/suprema/www/unifingerui/CommandCall");
        SetReadCallbackFunctionName("ReadPacketCallback");
        SetWriteCallbackFunctionName("SendPacketCallback");
    }

    private int SendPacketCallback(String var)  {
        int returnSize = m_SerialConnect.SendPacket(var);
        return returnSize;
    }

    private int ReadPacketCallback(int size, byte[] read_buffer) throws InterruptedException, TimeoutException, ExecutionException {

        int readSize = m_SerialConnect.ReadPacket(size,read_buffer);

        return readSize;
    }

    public void DisplayIdentifyResult(int userID)
    {
        if(userID>=0)
            startMessage(String.valueOf(userID));
        else
            startMessage("Not found");
    }

    private void startMessage(String str) {
        Message msg = m_MainEditHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("Identify_Result", str);
        msg.setData(bundle);
        m_MainEditHandler.sendMessage(msg);
    }

}
