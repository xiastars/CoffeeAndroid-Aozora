package com.balanx.nfhelper.dialog;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import com.balanx.nfhelper.R;
import com.balanx.nfhelper.utils.SUtils;

/**
 * 中间弹出框基本样式
 * Created by xiaqiliang on 2017/6/20.
 */

public abstract class BaseFullwidthCenterDialog extends BaseDialog {

    public BaseFullwidthCenterDialog(@NonNull Context context) {
        super(context, R.style.TagFullScreenDialog);
        this.context = context;
        SUtils.initScreenDisplayMetrics((Activity) context);
        setDialogCenterAndWidthFullscreen();
        setCanceledOnTouchOutside(true);
    }

    public BaseFullwidthCenterDialog(@NonNull Context context, int style) {
        super(context, style);
        this.context = context;
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected int showEnterAnim() {
        return R.anim.dialog_center_enter;
    }

    @Override
    protected int showQuitAnim() {
        return R.anim.dialog_center_quit;
    }

}
