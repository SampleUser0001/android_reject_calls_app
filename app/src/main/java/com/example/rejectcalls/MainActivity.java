package com.example.rejectcalls;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String PREFS_NAME = "RejectCallsPrefs";
    private static final String BLOCKED_NUMBERS_KEY = "blockedNumbers";

    private EditText phoneNumberEditText;
    private Button addButton;
    private ListView blockedNumbersListView;
    private ArrayList<String> blockedNumbers;
    private ArrayAdapter<String> adapter;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初期化
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        addButton = findViewById(R.id.addButton);
        blockedNumbersListView = findViewById(R.id.blockedNumbersListView);

        // SharedPreferencesから拒否リストを読み込む
        preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> blockedNumbersSet = preferences.getStringSet(BLOCKED_NUMBERS_KEY, new HashSet<>());
        blockedNumbers = new ArrayList<>(blockedNumbersSet);

        // リストビューのアダプターを設定
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, blockedNumbers);
        blockedNumbersListView.setAdapter(adapter);

        // 長押しで削除機能を追加
        blockedNumbersListView.setOnItemLongClickListener((parent, view, position, id) -> {
            String numberToRemove = blockedNumbers.get(position);
            blockedNumbers.remove(position);
            saveBlockedNumbers();
            adapter.notifyDataSetChanged();
            Toast.makeText(MainActivity.this, getString(R.string.number_removed), Toast.LENGTH_SHORT).show();
            return true;
        });

        // 追加ボタンのクリックリスナー
        addButton.setOnClickListener(v -> {
            String number = phoneNumberEditText.getText().toString().trim();
            if (!number.isEmpty()) {
                blockedNumbers.add(number);
                saveBlockedNumbers();
                adapter.notifyDataSetChanged();
                phoneNumberEditText.setText("");
                Toast.makeText(MainActivity.this, getString(R.string.number_added), Toast.LENGTH_SHORT).show();
            }
        });

        // 必要な権限をチェック
        checkPermissions();
    }

    // 拒否リストをSharedPreferencesに保存
    private void saveBlockedNumbers() {
        Set<String> blockedNumbersSet = new HashSet<>(blockedNumbers);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(BLOCKED_NUMBERS_KEY, blockedNumbersSet);
        editor.apply();
    }

    // 権限チェック
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.ANSWER_PHONE_CALLS
                }, PERMISSION_REQUEST_CODE);
            }
            
            // Android 9以上の場合、コールスクリーニングサービスの設定画面へ誘導
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (!allGranted) {
                Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_LONG).show();
            }
        }
    }
}
