package com.wma.autoaccessbilityservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText mContentEt, mPackageEt;
    Button mCompleteBtn, mAddBtn;
    RecyclerView mRecyclerView;
    List<String> mContentList;
    String mPackageName;
    MyRecyclerAdapter mAdapter;

    String defaultPackage = "com.taobao.taobao";


    MyResultReceiver resultReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("WMA-WMA", "onCreate: MainActivity");
        mContentEt = findViewById(R.id.et_content);
        mAddBtn = findViewById(R.id.btn_add);
        mPackageEt = findViewById(R.id.et_package_name);
        mRecyclerView = findViewById(R.id.recycler_view);
        mCompleteBtn = findViewById(R.id.btn_complete);
        mCompleteBtn.setOnClickListener(this);
        mAddBtn.setOnClickListener(this);
        mContentList = new ArrayList<>();
        mAdapter = new MyRecyclerAdapter(mContentList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mPackageEt.setText(defaultPackage);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(resultReceiver != null){
            unregisterReceiver(resultReceiver);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                Editable text = mContentEt.getText();
                if (!TextUtils.isEmpty(text)) {
                    String content = text.toString();
                    mContentList.add(content);
                    mAdapter.notifyDataSetChanged();
                    mContentEt.setText("");
                } else {
                    Toast.makeText(MainActivity.this, "请输入需要点击的内容", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_complete:

                Editable text2 = mPackageEt.getText();
                if (!TextUtils.isEmpty(text2)) {
                    mPackageName = text2.toString();
                } else {
                    Toast.makeText(MainActivity.this, "请输入要自动操作的软件包名", Toast.LENGTH_SHORT).show();
                }

                if (mContentList.size() <= 0) {
                    Toast.makeText(MainActivity.this, "请完善点击信息", Toast.LENGTH_SHORT).show();
                    return;
                }


                Log.d("WMA-WMA", "onClick: 是否开启 + " + isAccessibilitySettingsOn(MainActivity.this));
                if (isAccessibilitySettingsOn(MainActivity.this)) {
                    Intent broadcastIntent = new Intent(Model.ACTION);
                    broadcastIntent.putExtra(Model.NAME, new Model(mPackageName, mContentList));
                    sendBroadcast(broadcastIntent);
                    finish();
                } else {
                    IntentFilter filter = new IntentFilter("onCreate");
                    resultReceiver   = new MyResultReceiver();
                    registerReceiver(resultReceiver,filter);
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    startActivity(intent);
                }
                break;
        }

    }


    // To check if service is enabled
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + StatusAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
//            Log.v("WMA-WMA", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("WMA-WMA", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
//            Log.v("WMA-WMA", "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

//                    Log.v("WMA-WMA", "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
//                        Log.v("WMA-WMA", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v("WMA-WMA", "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }

    public  class MyResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("onCreate")){
                Intent broadcastIntent = new Intent(Model.ACTION);
                broadcastIntent.putExtra(Model.NAME, new Model(mPackageName, mContentList));
                sendBroadcast(broadcastIntent);
                finish();
            }
        }
    }
}
