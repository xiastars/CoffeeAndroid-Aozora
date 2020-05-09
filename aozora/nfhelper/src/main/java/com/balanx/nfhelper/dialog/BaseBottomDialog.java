package com.balanx.nfhelper.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.balanx.nfhelper.R;

/**
 * 底部弹出框基本样式
 * Created by xiaqiliang on 2017/6/20.
 */

public abstract class BaseBottomDialog extends BaseDialog {

    public BaseBottomDialog(@NonNull Context context) {
        super(context, R.style.TagFullScreenDialog);
        this.context = context;
    }

    public BaseBottomDialog(@NonNull Context context, int style) {
        super(context, style);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(true);
        setDialogBottom();

    }

    @Override
    protected int showEnterAnim() {
        return R.anim.slide_up;
    }

    @Override
    protected int showQuitAnim() {
        return R.anim.slide_bottom;
    }

}
