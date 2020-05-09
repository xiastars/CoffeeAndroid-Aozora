package com.summer.asozora.livedoor.view;

import android.app.Activity;
import android.app.Dialog;

import com.summer.asozora.livedoor.R;
import com.summer.asozora.livedoor.UIApplication;

public class LoadingDialog {

    /**
     * 加载数据对话框
     */
    private static Dialog mLoadingDialog;


    /**
     * 显示加载对话框
     *
     * @param context    上下文
     * @param msg        对话框显示内容
     * @param cancelable 对话框是否可以取消
     */
    public static Dialog showDialogForLoading(Activity context, String msg, boolean cancelable) {

        mLoadingDialog = DialogHelper.getProgressDialog(context,msg, cancelable);

        mLoadingDialog.setCancelable(cancelable);
        try{
            mLoadingDialog.show();
        }catch (Exception e){
          e.printStackTrace();
        }
        return mLoadingDialog;
    }

    public static Dialog showDialogForLoading(Activity context) {
        return showDialogForLoading(context, UIApplication.getAppContext().getString(R.string.loading), true);
    }

    /**
     * 关闭加载对话框
     */
    public static void cancelDialogForLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.cancel();
        }
    }
}
