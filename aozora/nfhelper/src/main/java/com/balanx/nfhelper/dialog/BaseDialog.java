package com.balanx.nfhelper.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.balanx.nfhelper.R;
import com.balanx.nfhelper.utils.Logs;
import com.balanx.nfhelper.utils.SUtils;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;

/**
 * 底部弹出框基本样式
 * Created by xiaqiliang on 2017/6/20.
 */

public abstract class BaseDialog extends Dialog {
    protected Context context;
    FrameLayout flParent;
    RelativeLayout rlParent;
    int parentRes;
    protected MyHandler myHandler;

    protected boolean isShowAnim = true;

    public BaseDialog(@NonNull Context context) {
        super(context, R.style.TagFullScreenDialog);
        this.context = context;
    }

    public BaseDialog(@NonNull Context context, int style) {
        super(context, style);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_base);
        myHandler = new MyHandler(this);
        rlParent = (RelativeLayout) findViewById(R.id.rl_base_parent);
        rlParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onParentClick();
            }
        });
        flParent = (FrameLayout) findViewById(R.id.fl_parent);
        View view = LayoutInflater.from(context).inflate(getContainerLayoutResId(), null);
        flParent.addView(view);
        ButterKnife.bind(this);
        initView(view);
    }

    protected void onParentClick() {
        cancelDialog();
    }

    /**
     * 将对话框置在底部，而且宽度全屏
     */
    protected void setDialogBottom() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = SUtils.screenWidth;
            lp.height = SUtils.screenHeight;
            lp.gravity = Gravity.BOTTOM;
        }
    }

    /**
     * 将对话框置在底部，而且宽度全屏
     */
    protected void setDialogPosition(final int left, final int top) {
        Window window = getWindow();
        if (window != null) {
            final WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = SUtils.getDip(context, 300);
            lp.height = SUtils.getDip(context, 200);
            lp.horizontalMargin = left - lp.width;
            lp.verticalMargin = top;

            lp.gravity = Gravity.TOP;
            Logs.i("margin::" + left + ",," + top + "<,," + lp.horizontalMargin);
        }
    }

    /**
     * 将中间显示的，设为全屏
     */
    protected void setDialogCenterAndWidthFullscreen() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = SUtils.screenWidth;
            lp.horizontalMargin = 0;
            lp.gravity = Gravity.CENTER;
        }
    }


    /**
     * 将中间显示的，设为全屏
     */
    protected void setDialogCenterAndWidthFullscreen(int margin) {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = SUtils.screenWidth-margin*2;


            lp.gravity = Gravity.CENTER;
        }
    }

    /**
     * 将对话框置在底部，而且宽度全屏
     */
    protected void setDialogBottomAndHeightFullscreen() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = SUtils.screenWidth;
            lp.height = SUtils.screenHeight;
            lp.gravity = Gravity.BOTTOM;
            Logs.i("height:" + lp.height);
        }
    }

    /**
     * 返回容器视图资源Id
     */
    protected int getContainerLayoutResId() {
        return setContainerView();
    }

    public abstract int setContainerView();

    public abstract void initView(View view);

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isShowAnim) {
            return;
        }
        int animRes = showEnterAnim();
        if (animRes != 0) {
            Animation anim = AnimationUtils.loadAnimation(context,
                    animRes);
            flParent.startAnimation(anim);
        }
    }

    /**
     * 返回对应颜色
     *
     * @param colorRes
     * @return
     */
    public int getResourceColor(int colorRes) {
        return context.getResources().getColor(colorRes);
    }

    /**
     * 取消显示必须调用此方法，展现动画
     */
    public void cancelDialog() {
        if (!isShowAnim) {
            BaseDialog.this.cancel();
            return;
        }
        int animRes = showQuitAnim();
        if (animRes != 0) {
            if (flParent == null) {
                cancel();
                return;
            }
            flParent.clearAnimation();
            Animation anim = AnimationUtils.loadAnimation(context,
                    animRes);
            flParent.startAnimation(anim);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    BaseDialog.this.cancel();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        } else {
            BaseDialog.this.cancel();
        }

    }

    protected void setParentRes(int parentRes) {
        this.parentRes = parentRes;
        rlParent.setBackgroundResource(parentRes);
    }

    /**
     * 设置是否显示动画
     *
     * @param showAnim
     */
    protected void setShowAnim(boolean showAnim) {
        this.isShowAnim = showAnim;
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            cancelDialog();
        }
        return false;
    }

    //显示出场动画
    protected abstract int showEnterAnim();

    //显示退场动画
    protected abstract int showQuitAnim();

    public static class MyHandler extends Handler {
        private final WeakReference<BaseDialog> mActivity;

        public MyHandler(BaseDialog activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseDialog activity = mActivity.get();
            if (null != activity) {
                switch (msg.what) {
                    default:
                        activity.handleMsg(msg.what, msg.obj);
                }
            }
        }
    }

    protected void handleMsg(int position, Object object) {

    }
}
