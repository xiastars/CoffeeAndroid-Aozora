package com.balanx.nfhelper.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.balanx.nfhelper.R;
import com.balanx.nfhelper.listener.DialogAfterClickListener;
import com.balanx.nfhelper.utils.SUtils;

/**
 * @Description: 常用的默认通知弹窗
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2019/4/30 16:20
 */
public class BaseSureDialog extends BaseCenterDialog {

    private DialogAfterClickListener listener;

    private int layoutid;
    private TextView tvContent;
    private static BaseSureDialog tipDialog = null;

    String name;
    String okContent;
    String content;

    boolean hideSureButton;

    boolean showTitle = true;

    public static BaseSureDialog getInstance(Context context, String name, DialogAfterClickListener listener) {
        if (tipDialog != null) {
            try {
                tipDialog.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
            tipDialog = null;
        }
        tipDialog = new BaseSureDialog(context, name, listener);
        return tipDialog;
    }

    public BaseSureDialog(Context context, String name, DialogAfterClickListener listener) {
        super(context, R.style.TagFullScreenDialog);
        this.listener = listener;
        this.name = name;
        layoutid = R.layout.dialog_know;
    }

    public BaseSureDialog(Context context, int layoutid,String name, DialogAfterClickListener listener) {
        super(context, R.style.TagFullScreenDialog);
        this.listener = listener;
        this.name = name;
        this.layoutid = layoutid;
    }

    public BaseSureDialog(Context context, int name, DialogAfterClickListener listener) {
        super(context, R.style.TagFullScreenDialog);
        this.listener = listener;
        this.name = context.getResources().getString(name);
        layoutid = R.layout.dialog_know;
    }

    @Override
    public int setContainerView() {
        return layoutid;
    }

    @Override
    public void initView(View view) {
        tvContent = (TextView) view.findViewById(R.id.tv_content);

        if (tvContent != null) {
            if (!TextUtils.isEmpty(name)) {
                SUtils.setHtmlText(name, tvContent);
            }
            if (!TextUtils.isEmpty(content)) {
                SUtils.setHtmlText(content, tvContent);
            }
        }
        TextView tvOK = (TextView) view.findViewById(R.id.tips_ok_tv);
        if (!TextUtils.isEmpty(okContent)) {
            tvOK.setText(okContent);
        }
        SUtils.clickTransColor(tvOK);
        tvOK.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onSure();
                }
                cancelDialog();
            }

        });
        View viewLine = view.findViewById(R.id.view_line);
        viewLine.setVisibility(hideSureButton ? View.GONE:View.VISIBLE);
        LinearLayout llBottom = view.findViewById(R.id.ll_bottom);
        llBottom.setVisibility(hideSureButton ? View.GONE:View.VISIBLE);
    }

    public void hideSureButton(boolean hide){
        this.hideSureButton = hide;
    }

    public void setOkContent(String content){
        this.okContent = content;
    }

    public void hideTitle() {
        showTitle = false;
    }

    public void setContent(String content) {
        this.content = content;
    }


}