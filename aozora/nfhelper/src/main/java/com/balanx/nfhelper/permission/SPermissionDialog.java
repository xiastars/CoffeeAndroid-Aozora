package com.balanx.nfhelper.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.TextView;

import com.balanx.nfhelper.R;
import com.balanx.nfhelper.dialog.BaseCenterDialog;
import com.balanx.nfhelper.utils.Logs;

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/6/21 17:07
 */
public class SPermissionDialog extends BaseCenterDialog {

    String permission;

    public SPermissionDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    public int setContainerView() {
        return R.layout.dialog_permission_single;
    }

    @Override
    public void initView(View view) {
        TextView tvContent = view.findViewById(R.id.tv_content);
        Logs.i("permission::"+permission);
        if (permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            tvContent.setText(R.string.permission_name_location);
        } else if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            tvContent.setText(context.getResources().getString(R.string.permission_name_write_storage));
        }
        TextView tvOk = view.findViewById(R.id.tv_ok);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, 123);
                cancelDialog();
            }
        });

    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
