package com.github.polurival.ipcmessenger;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView mTxtResult;
    private EditText mEditText;

    private Messenger mMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTxtResult = findViewById(R.id.txtResult);
        Button button = findViewById(R.id.button);
        mEditText = findViewById(R.id.editText);

        // Service Connection to handle system callbacks
        ServiceConnection serviceConnection = new ServiceConnectionImpl();

        // We bind to the service
        bindService(new Intent(this, ConvertService.class), serviceConnection, Context.BIND_AUTO_CREATE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String val = mEditText.getText().toString();
                Message msg = Message.obtain(null, ConvertService.TO_UPPER_CASE);
                msg.replyTo = new Messenger(new ResponseHandler());
                // We pass the value
                Bundle bundle = new Bundle();
                bundle.putString("data", val);

                msg.setData(bundle);

                try {
                    mMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class ServiceConnectionImpl implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            // We are connected to the service
            mMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mMessenger = null;
        }
    }

    /**
     * This class handles the Service response
     */
    class ResponseHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int responseCode = msg.what;

            switch (responseCode) {
                case ConvertService.TO_UPPER_CASE_RESPONSE:
                    Log.d(TAG, "main process handle message from service");
                    String result = msg.getData().getString("respData");
                    mTxtResult.setText(result);
            }
        }
    }
}
