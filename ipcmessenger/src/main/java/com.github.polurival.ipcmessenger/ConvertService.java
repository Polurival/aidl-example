package com.github.polurival.ipcmessenger;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class ConvertService extends Service {

    private static final String TAG = "ConvertService";

    public static final int TO_UPPER_CASE = 1;
    public static final int TO_UPPER_CASE_RESPONSE = 2;

    private Messenger messenger = new Messenger(new ConvertHandler());

    public ConvertService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    static class ConvertHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // This is the action
            int msgType = msg.what;

            switch (msgType) {
                case TO_UPPER_CASE:
                    Log.d(TAG, "service process handle message from main");
                    // Incoming data
                    String data = msg.getData().getString("data");
                    Message resp = Message.obtain(null, TO_UPPER_CASE_RESPONSE);
                    Bundle bundleResp = new Bundle();
                    bundleResp.putString("respData", data == null ? "" : data.toUpperCase());
                    resp.setData(bundleResp);

                    try {
                        msg.replyTo.send(resp);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
}
