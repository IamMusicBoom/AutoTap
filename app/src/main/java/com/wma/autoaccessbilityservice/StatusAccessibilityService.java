package com.wma.autoaccessbilityservice;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import java.util.List;

/**
 * Created by 王明骜 on 19-11-4 下午1:13.
 */
public class StatusAccessibilityService extends AccessibilityService {
    List<String> mContentList = null;
    String mPackageName = null;

    UpdateReceiver updateReceiver;

    @Override
    public void onCreate() {
        Log.d("WMA-WMA", "onCreate: StatusAccessibilityService");
        super.onCreate();
        updateReceiver = new UpdateReceiver();
        IntentFilter filter = new IntentFilter(Model.ACTION);
        registerReceiver(updateReceiver,filter);

        Intent intent = new Intent("onCreate");
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(updateReceiver);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (TextUtils.isEmpty(mPackageName) || mContentList == null || mContentList.size() <= 0) {
            return;
        }
        CharSequence charSequence = event.getPackageName();
        String packageName = "";
        if (charSequence != null) {
            packageName = charSequence.toString();
        }
        int eventType = event.getEventType();
        if (!packageName.equals(mPackageName)) {
            return;
        }
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                for (int i = 0; i < mContentList.size(); i++) {
                    String content = mContentList.get(i);
                    if(rootInActiveWindow == null){
                        return;
                    }
                    List<AccessibilityNodeInfo> registers = rootInActiveWindow.findAccessibilityNodeInfosByText(content);
                    if (registers != null && registers.size() > 0) {
                        AccessibilityNodeInfo register = registers.get(0);
                        register.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        Log.d("WMA-WMA", "onAccessibilityEvent: accessibilityNodeInfo = " + register);
                    }else{
                        continue;
                    }
                }




//                List<AccessibilityNodeInfo> registers = rootInActiveWindow.findAccessibilityNodeInfosByText("注册/登录");
//                if (registers != null && registers.size() > 0) {
//                    AccessibilityNodeInfo register = registers.get(0);
//                    register.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    Log.d("WMA-WMA", "onAccessibilityEvent: accessibilityNodeInfo = " + register);
//                }
//                if (rootInActiveWindow == null) {
//                    return;
//                }
//                AccessibilityNodeInfo child = rootInActiveWindow.getChild(2);
//                AccessibilityNodeInfo typeInPhone = child.getChild(0);
//                if (typeInPhone != null) {
//                    Bundle arguments = new Bundle();
//                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "13541100067");
//                    typeInPhone.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
//                    Log.d("WMA-WMA", "onAccessibilityEvent: accessibilityNodeInfo = " + typeInPhone);
//                }
//
//                List<AccessibilityNodeInfo> verifyCodes = child.findAccessibilityNodeInfosByText("获取验证");
//                if (verifyCodes != null && verifyCodes.size() > 0) {
//                    AccessibilityNodeInfo verifyCode = verifyCodes.get(0);
//                    verifyCode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    Log.d("WMA-WMA", "onAccessibilityEvent: accessibilityNodeInfo = " + verifyCode);
//                }
                break;
        }


    }

    @Override
    public void onInterrupt() {

    }


    class UpdateReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Model.ACTION)){
                Model model = (Model) intent.getSerializableExtra(Model.NAME);
                mPackageName = model.getPackageName();
                mContentList = model.getContentList();
                Log.d("WMA-WMA", "onReceive: mPackageName = " + mPackageName);
                Log.d("WMA-WMA", "onReceive: mContentList = " + mContentList);
            }
        }
    }

}
