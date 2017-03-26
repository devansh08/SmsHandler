package com.example.devansh.smshandler;

import android.Manifest;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 10;
    public static String MESSAGE_TEXT = "";

    EditText msgString;
    Button setString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate", "MainActivity created");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        msgString = (EditText) findViewById(R.id.editText);
        setString = (Button) findViewById(R.id.button);

        setString.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMessageString();
            }
        });

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            enableBroadcastReceiver();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case PERMISSION_REQUEST_CODE : {
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableBroadcastReceiver();
                } else {
                    disableBroadcastReceiver();
                }
            }
            break;
            default : {
                Log.d("PermissionRequest", "requestCode error");
                Toast.makeText(this, "Error while requesting permissions", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void enableBroadcastReceiver() {

        ComponentName receiver = new ComponentName(this, SmsReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Log.d("enableReceiver", "Broadcast receiver enabled");
        Toast.makeText(this, "Broadcast receiver enabled", Toast.LENGTH_SHORT).show();
    }

    public void disableBroadcastReceiver() {
        ComponentName receiver = new ComponentName(this, SmsReceiver.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Log.d("disableReceiver", "Broadcast receiver disabled");
        Toast.makeText(this, "Broadcast receiver disabled", Toast.LENGTH_SHORT).show();
    }

    public void setMessageString() {
        MESSAGE_TEXT = msgString.getText().toString();
        Log.d("setMsgString", MESSAGE_TEXT);
    }

    public int getMessageLength() {
        Log.d("getMsgLength", MESSAGE_TEXT + "Length : " + MESSAGE_TEXT.length());
        return MESSAGE_TEXT.length();
    }

    public String getMessageString() {
        return MESSAGE_TEXT;
    }
}
