package com.example.rejectcalls;

import android.content.SharedPreferences;
import android.os.Build;
import android.telecom.Call;
import android.telecom.Connection;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.HashSet;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.P)
public class CallScreeningService extends android.telecom.CallScreeningService {
    private static final String TAG = "CallScreeningService";
    private static final String PREFS_NAME = "RejectCallsPrefs";
    private static final String BLOCKED_NUMBERS_KEY = "blockedNumbers";

    @Override
    public void onScreenCall(Call.Details callDetails) {
        String phoneNumber = callDetails.getHandle().getSchemeSpecificPart();
        Log.d(TAG, "着信スクリーニング: " + phoneNumber);

        // 拒否リストをチェック
        if (shouldRejectCall(phoneNumber)) {
            Log.d(TAG, "着信拒否: " + phoneNumber);
            
            CallResponse.Builder response = new CallResponse.Builder();
            response.setDisallowCall(true);
            response.setRejectCall(true);
            response.setSkipCallLog(false);
            response.setSkipNotification(false);
            
            respondToCall(callDetails, response.build());
        } else {
            Log.d(TAG, "着信許可: " + phoneNumber);
            
            CallResponse.Builder response = new CallResponse.Builder();
            response.setDisallowCall(false);
            response.setRejectCall(false);
            
            respondToCall(callDetails, response.build());
        }
    }

    // 着信を拒否すべきかチェック
    private boolean shouldRejectCall(String incomingNumber) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> blockedNumbersSet = preferences.getStringSet(BLOCKED_NUMBERS_KEY, new HashSet<>());
        
        // 前方一致でチェック
        for (String blockedNumber : blockedNumbersSet) {
            if (incomingNumber.startsWith(blockedNumber)) {
                return true;
            }
        }
        
        return false;
    }
}
