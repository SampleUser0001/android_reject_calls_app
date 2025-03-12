package com.example.rejectcalls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class CallReceiver extends BroadcastReceiver {
    private static final String TAG = "CallReceiver";
    private static final String PREFS_NAME = "RejectCallsPrefs";
    private static final String BLOCKED_NUMBERS_KEY = "blockedNumbers";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            
            // 着信状態の場合
            if (TelephonyManager.EXTRA_STATE_RINGING.equals(state)) {
                String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                
                if (incomingNumber != null) {
                    Log.d(TAG, "着信: " + incomingNumber);
                    
                    // 拒否リストをチェック
                    if (shouldRejectCall(context, incomingNumber)) {
                        Log.d(TAG, "着信拒否: " + incomingNumber);
                        rejectCall(context);
                    }
                }
            }
        } else if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // 端末起動時の処理（必要に応じて）
            Log.d(TAG, "端末起動完了");
        }
    }

    // 着信を拒否すべきかチェック
    private boolean shouldRejectCall(Context context, String incomingNumber) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> blockedNumbersSet = preferences.getStringSet(BLOCKED_NUMBERS_KEY, new HashSet<>());
        
        // 前方一致でチェック
        for (String blockedNumber : blockedNumbersSet) {
            if (incomingNumber.startsWith(blockedNumber)) {
                return true;
            }
        }
        
        return false;
    }

    // 着信を拒否する
    private void rejectCall(Context context) {
        try {
            // Android 9.0未満の場合はリフレクションを使用
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            Class<?> telephonyClass = Class.forName(telephonyManager.getClass().getName());
            Method method = telephonyClass.getDeclaredMethod("endCall");
            method.invoke(telephonyManager);
        } catch (Exception e) {
            Log.e(TAG, "着信拒否に失敗: " + e.getMessage());
        }
    }
}
