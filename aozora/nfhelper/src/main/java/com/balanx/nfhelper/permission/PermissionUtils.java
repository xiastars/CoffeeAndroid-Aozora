package com.balanx.nfhelper.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.balanx.nfhelper.R;
import com.balanx.nfhelper.dialog.BaseSureDialog;
import com.balanx.nfhelper.listener.DialogAfterClickListener;
import com.balanx.nfhelper.utils.Logs;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;

import java.util.List;

/**
 * Created by dell on 2017/6/13.
 */

public class PermissionUtils {
    /**
     * 检查有没有读取权限
     *
     * @param context
     * @return
     */
    public static boolean checkReadPermission(
            final Context context) {
        return checkPermmision(context, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public static boolean checkWritePermission(
            final Context context) {
        return checkPermmision(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 检查有没有联系人读取权限
     *
     * @param context
     * @return
     */
    public static boolean checkPhonePermission(
            final Context context) {
        return checkPermmision(context, Manifest.permission.READ_PHONE_STATE);
    }

    public static boolean checkLocationPermission(final Context context) {
        return checkPermmision(context, Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    /**
     * 判断GPS有没有开启，没有的话主动开启
     *
     * @param context
     * @return
     */
    public static boolean checkGPS(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            LocationManager locationManager
                    = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!gps && !network) {
                BaseSureDialog baseSureDialog = new BaseSureDialog(context, context.getResources().getString(R.string.open_ble_hint), new DialogAfterClickListener() {
                    @Override
                    public void onSure() {
                        // 转到手机设置界面，用户设置GPS
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        ((Activity) context).startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                baseSureDialog.setOkContent(context.getResources().getString(R.string.go_to_setting));
                baseSureDialog.show();
                return false;
            }

        }
        return true;
    }

    /**
     * 检查有没有联系人读取权限
     *
     * @param context
     * @return
     */
    public static boolean checkAlertPermission(
            final Context context) {
        return checkPermmision(context, Manifest.permission.SYSTEM_ALERT_WINDOW);
    }

    /**
     * 检查有没有相机权限
     *
     * @param context
     * @return
     */
    public static boolean checkCameraPermission(
            final Context context) {
        return checkPermmision(context, Manifest.permission.CAMERA);
    }

    /**
     * 检查有没有录音权限
     *
     * @param context
     * @return
     */
    public static boolean checkRecordPermission(
            final Context context) {
        return checkPermmision(context, Manifest.permission.RECORD_AUDIO);
    }

    /**
     * 请求权限，拒绝时再次提醒
     *
     * @param activity
     * @param args
     */
    public static void rationRequestPermission(final Activity activity, String... args) {
        Rationale rationale = new DefaultRationale();
        Context context = activity;
        final PermissionSetting permissionSetting = new PermissionSetting(activity);
        AndPermission.with(activity)
                .permission(args)
                .rationale(rationale)//大家讲道理
                .onGranted(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {

                    }
                })
                .onDenied(new Action() {
                    @Override
                    public void onAction(List<String> permissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(activity, permissions)) {
                            //总是拒绝
                            permissionSetting.showSetting(permissions);
                        }
                    }
                })
                .start();
    }

    public static void showPermissionDialog(Context context, String... permissions) {
        if (!checkPermission(context, permissions)) {
            PermissionDialog permissionDialog = new PermissionDialog(context, permissions);
            permissionDialog.show();
        }
    }

    /**
     * 检测权限是否打开
     */
    private static boolean checkPermission(Context context, String... permissions) {
        boolean isChecked = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String p : permissions) {
                if (ContextCompat.checkSelfPermission(context,
                        p) != PackageManager.PERMISSION_GRANTED) {
                    isChecked = false;
                }
            }

        }
     /*   if(isChecked){
            isChecked = SUtils.getBooleanData(context,"protocal_checked");
        }*/
        return isChecked;
    }

    private static boolean checkPermmision(final Context context, final String permission) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        Logs.i("当前版本号:" + currentAPIVersion);
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    permission) != PackageManager.PERMISSION_GRANTED) {
                Logs.i("当前版本号:" + currentAPIVersion);
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        permission)) {
                    Logs.i("当前版本号:" + currentAPIVersion);
                    SPermissionDialog dialog = new SPermissionDialog(context);
                    dialog.setPermission(permission);
                    dialog.show();
                    Logs.i("请求权限");
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{permission}, 123);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
