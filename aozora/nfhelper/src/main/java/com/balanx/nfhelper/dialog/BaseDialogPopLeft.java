package com.balanx.nfhelper.dialog;

import android.content.Context;
import android.support.annotation.NonNull;

import com.balanx.nfhelper.R;

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/8/22 17:21
 */
public abstract class BaseDialogPopLeft extends BaseDialog {

    public BaseDialogPopLeft(@NonNull Context context,int left,int top) {
        super(context, R.style.TagFullScreenDialog);
        this.context = context;
        setCanceledOnTouchOutside(true);
        setDialogPosition(left ,top);
    }

    public BaseDialogPopLeft(@NonNull Context context, int style) {
        super(context, style);
        this.context = context;
        setCanceledOnTouchOutside(true);

    }

    @Override
    protected int showEnterAnim() {
        return R.anim.dialog_pop_left;
    }

    @Override
    protected int showQuitAnim() {
        return R.anim.dialog_center_quit;
    }
}
