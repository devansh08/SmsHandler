package com.example.devansh.smshandler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

    MainActivity mainActivity = new MainActivity();

    private static String MESSAGE_KEY_TEXT = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("onReceive", "Message received");
        Toast.makeText(context, "Message received", Toast.LENGTH_LONG).show();
        Bundle bundle = intent.getExtras();
        SmsMessage[] smsMessages;
        String msgBody = "";
        String msgSender = "";
        boolean chk;

        if(bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            assert pdus != null;
            smsMessages = new SmsMessage[pdus.length];

            for(int i = 0; i < smsMessages.length; i++) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String format = bundle.getString("format");
                    smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                }
                else {
                    //noinspection deprecation
                    smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                msgBody += smsMessages[i].getMessageBody();
                msgSender += smsMessages[i].getOriginatingAddress();
            }
            Log.d("onReceive", "Sender : " + msgSender);
            Log.d("onReceive", "Body : " + msgBody);
            chk = chkMessage(msgBody);

            if(chk) {
                Log.d("onReceive", "Message correct");
                //notifyUser(context);
                try {
                    notifyUser(context);
                } catch (InterruptedException e) {
                    Log.e("notifyUser", "Exception in TimeUnit");
                }
            }
        }
    }

    public boolean chkMessage(String body){
        String string, msgSubString;
        int stringLength;

        Log.d("chkMessage", "Function start");
        stringLength = mainActivity.getMessageLength();
        Log.d("chkMessage", "Length : " + stringLength);

        if(stringLength != 0) {
            string = mainActivity.getMessageString();
            MESSAGE_KEY_TEXT = string;
            msgSubString = body.substring(0, stringLength);

            Log.d("string", string);
            Log.d("msgSubString", msgSubString);

            return msgSubString.equals(string);
        } else {
            if(MESSAGE_KEY_TEXT.length() == 0) {
                return false;
            }
            else {
                msgSubString = body.substring(0, MESSAGE_KEY_TEXT.length());

                Log.d("string", MESSAGE_KEY_TEXT);
                Log.d("msgSubString", msgSubString);

                return msgSubString.equals(MESSAGE_KEY_TEXT);
            }
        }
    }

    public void notifyUser(Context context) throws InterruptedException {
        Uri ringtoneUri;
        final Ringtone ringtone;

        ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        ringtone = RingtoneManager.getRingtone(context, ringtoneUri);

        Log.d("notifyUser", "Ringtone initialized");
        if (ringtone != null) {
            Log.d("Ringtone", "Playing ringtone");
            Toast.makeText(context, "Playing ringtone", Toast.LENGTH_LONG).show();
            ringtone.play();
            
            Log.d("Ringtone", "Ringtone played");
        }
        else {
            Log.d("ringtone", "Ringtone is null");
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                assert ringtone != null;
                if(ringtone.isPlaying()) {
                    ringtone.stop();
                }
            }
        }, 10000);
        Log.d("notifyUser", "Ringtone stopped");
    }
}
