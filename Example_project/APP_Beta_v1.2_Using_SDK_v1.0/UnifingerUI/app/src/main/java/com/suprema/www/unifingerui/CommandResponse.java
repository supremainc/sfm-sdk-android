package com.suprema.www.unifingerui;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

/**
 * Created by jmlee on 2017-04-05.
 */
public class CommandResponse {
    UnifingerMain m_MainContext;

    public CommandResponse(UnifingerMain MainContext)
    {
        m_MainContext = MainContext;
    }

   public void DisplayIdentifyResult(int userID)
   {
       if(userID>=0)
           startMessage(String.valueOf(userID));
       else
           startMessage("Not found");
   }

    private void startMessage(String str) {
        Message msg = m_MainContext.SetUserID_Edit.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("Identify_Result", str);
        msg.setData(bundle);
        m_MainContext.SetUserID_Edit.sendMessage(msg);
    }


}
