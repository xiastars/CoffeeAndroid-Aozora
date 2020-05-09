package com.balanx.nfhelper.permission;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.balanx.nfhelper.R;
import com.balanx.nfhelper.dialog.BaseCenterDialog;
import com.balanx.nfhelper.utils.SUtils;

/**
 * @Description: 权限提示
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/6/19 15:42
 */
public class PermissionDialog extends BaseCenterDialog {

    TextView tvTitle;
    LinearLayout llContainer;
    String[] permissions;
    TextView tvProtocal;
    AppCompatCheckBox ckProtocal;
    Switch switchLocation;
    Switch switchWrite;

    boolean stopCheckState = true;//是否是检查状态

    public PermissionDialog(@NonNull Context context, String... permissions) {
        super(context);
        this.permissions = permissions;
    }

    @Override
    public int setContainerView() {
        return R.layout.dialog_permission;
    }

    @Override
    public void initView(View view) {
        setCanceledOnTouchOutside(false);
        ckProtocal = view.findViewById(R.id.ck_protocal);
        tvTitle = view.findViewById(R.id.tv_title);
        llContainer = view.findViewById(R.id.ll_container);
        tvProtocal = view.findViewById(R.id.tv_protocol);
        myHandler.sendEmptyMessageDelayed(0, 1000);
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addPermissionView();
            }
        }, 400);
        ckProtocal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SUtils.saveBooleanData(context,"protocal_checked",isChecked);
            }
        });
        tvProtocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.sendBroadcast(new Intent(("VIEW_PROTOCAL")));
                //WebContainerActivity.show(context, "http://www.balanxems.com/protocol/","用户协议");
            }
        });

    }

    private void addPermissionView() {
        for (String p : permissions) {
            if (p.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                switchLocation = addPermissionView(p, R.string.permission_name_location, R.string.permission_location_detail);

            } else if (p.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                switchWrite = addPermissionView(p, R.string.permission_name_write_storage, R.string.permission_storage_detail);
            }
        }
    }

    private Switch addPermissionView(final String permission, int titleRes, int detail) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_permission, null);
        llContainer.addView(view);
        TextView tvTitle = view.findViewById(R.id.tv_permission);
        tvTitle.setText(titleRes);
        TextView tvDetail = view.findViewById(R.id.tv_detail);
        tvDetail.setText(detail);
        final Switch vSwitch = view.findViewById(R.id.view_switch);
        vSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Intent gpsIntent = new Intent();
                    gpsIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                    gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
                    gpsIntent.setData(Uri.parse("custom:3"));
                    try {
                        PendingIntent.getBroadcast(context, 0, gpsIntent, 0).send();
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, 123);
            }
        });
        return vSwitch;

    }

    /**
     * 检测权限是否打开
     */
    private int checkPermission() {
      int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String p : permissions) {
                if (ContextCompat.checkSelfPermission(context,
                        p) != PackageManager.PERMISSION_GRANTED) {
                    if (p.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        return 1;
                    }else{
                        return 2;
                    }
                }
            }

        }
       if(result == 0){
            boolean isChecked = SUtils.getBooleanData(context,"protocal_checked");
            if(!isChecked){
               // return 3;
            }
        }
        return 0;
    }

    @Override
    protected void handleMsg(int position, Object object) {
        switch (position) {
            case 0:
                int result = checkPermission();
                if (result== 0) {

                    context.sendBroadcast(new Intent("location_opend"));
                    myHandler.removeMessages(0);

                    cancelDialog();
                } else {
                    if(stopCheckState){
                        if(result == 1){
                            switchLocation.setChecked(false);
                            switchLocation.setChecked(true);

                        }else if(result == 2){
                            switchWrite.setChecked(false);
                            switchWrite.setEnabled(true);
                        }
                    }

                    myHandler.sendEmptyMessageDelayed(0, 1500);
                }
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        stopCheckState = hasFocus;
    }
}
