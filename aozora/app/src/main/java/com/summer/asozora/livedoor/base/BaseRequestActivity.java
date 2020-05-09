package com.summer.asozora.livedoor.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balanx.nfhelper.server.SummerParameter;
import com.balanx.nfhelper.utils.BitmapUtils;
import com.balanx.nfhelper.utils.CUtils;
import com.balanx.nfhelper.utils.Logs;
import com.balanx.nfhelper.utils.SUtils;
import com.balanx.nfhelper.view.RRelativeLayout;
import com.summer.asozora.livedoor.R;
import com.summer.asozora.livedoor.view.LoadingDialog;
import com.summer.resp.BaseResp;

import java.lang.ref.WeakReference;
import java.util.NoSuchElementException;

import butterknife.ButterKnife;

/**
 * Created by xiaqiliang on 2017/3/24.
 */
public abstract class BaseRequestActivity extends Activity {

    protected String TAG = "fsxq";
    TextView tvTitle;
    public FrameLayout flContainer;
    RelativeLayout rlBaseTop;
    RRelativeLayout rlTitle;
    View viewBack;
    public Context context;
    protected MyHandler myHandlder;
    public int pageIndex;
    protected long fromId;
    public String lastId;
    protected boolean toastMessage;//是否弹出错误信息
    protected boolean isReturnFailureMsg;//请求错误状态下是否返回
    boolean isRefresh;
    public BaseHelper baseHelper;
    View line1;
    Resources resources;
    public TextView rightTv;
    protected LinearLayout llBack;

    protected String BACK_TAG = "Other";
    protected int DEFAULT_LIMIT = 10;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        SUtils.initScreenDisplayMetrics(this);
        myHandlder = new MyHandler(this);
        baseHelper = new BaseHelper(context, myHandlder);
        setContentView(R.layout.activity_base);

        initTitleView();
        checkView();
    }

    /**
     * @param backTag
     */
    public void setBackTag(String backTag) {
        BACK_TAG = backTag;
    }

    private void checkView() {
        baseHelper.setMIUIStatusBarDarkMode(this);
        if (!SUtils.isNetworkAvailable(context)) {
          /*  View view = LayoutInflater.from(this).inflate(R.layout.view_network_broken, null);
            flContainer.addView(view);*/
        } else {

        }
        initContentView();
        ButterKnife.bind(this);
        initPresenter();
        initData();

    }

    /**
     * 自定义错误
     * @param iconRes
     * @param content
     * @param tryContent
     * @param listener
     */
    public void showErrorView(int iconRes, String content, String tryContent,View.OnClickListener listener){
        flContainer.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.view_network_broken, null);
        ImageView ivError = view.findViewById(R.id.iv_nav);
        SUtils.setPicResource(ivError,iconRes);
        TextView tvContent = view.findViewById(R.id.tv_hint_content);
        tvContent.setText(content);
        TextView tvTry = view.findViewById(R.id.tv_reload);
        tvTry.setText(tryContent);
        flContainer.addView(view);
        TextView tvReload = view.findViewById(R.id.tv_reload);
        tvReload.setOnClickListener(listener);
    }

    protected void showRightView(String content, View.OnClickListener listener) {
        rightTv.setVisibility(View.VISIBLE);
        rightTv.setText(content);
        rightTv.setOnClickListener(listener);
    }

    /*
     *//**
     * 初始化分享按钮
     *//*
    protected void initShareButton(final OnShareListener listener) {
        this.listener = listener;
        Button btnShare = (Button) findViewById(R.id.btn_share);
        btnShare.setVisibility(View.VISIBLE);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onShare();
                }
            }
        });
    }*/

/*    protected void setShareListener(OnShareListener listener) {
        this.listener = listener;
    }*/

    /**
     * 如果不想要默认的头部，则移除，自己在Activity里写
     */
    public void removeTitle() {
        rlTitle.setVisibility(View.GONE);
        line1.setVisibility(View.GONE);
    }

  /*  protected void setBlankTitleView() {
        setBlankTitleView(true);
    }*/

/*
    protected View setBlankTitleView(boolean isTopMargin) {
        removeTitle();
        RRelativeLayout view = (RRelativeLayout) LayoutInflater.from(context).inflate(R.layout.include_back, null);
        ImageView viewBack = (ImageView) view.findViewById(R.id.view_back);
        viewBack.setBackgroundResource(R.drawable.title_icon_return_default);
        flContainer.addView(view);
        SViewUtils.setViewHeight(view, 49);
        setLayoutFullscreen();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.topMargin = SUtils.getStatusBarHeight(this) + (isTopMargin ? SUtils.getDip(context, 20) : 0);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClick();
            }
        });
        return view;
    }
*/


    /*    */

    /**
     * 当此界面有EditText时，Full模式下不弹出 ，所以不能设为Full模式
     *//*
    protected void setBlankTitleViewWithEditMode() {
        removeTitle();
        RLinearLayout view = (RLinearLayout) LayoutInflater.from(context).inflate(R.layout.include_back, null);
        TextView viewBack = (TextView) view.findViewById(R.id.view_back);
        viewBack.setBackgroundResource(R.drawable.title_icon_return_default);
        flContainer.addView(view);
        view.reLayout(55, 45, 0, 0);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.topMargin = SUtils.getDip(context, 20);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClick();
            }
        });
    }*/
    private void initTitleView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        if (setTitleId() != 0) {
            tvTitle.setText(getString(setTitleId()));
        }
        rlBaseTop = (RelativeLayout) findViewById(R.id.rl_base_parent);
        rlTitle = (RRelativeLayout) findViewById(R.id.title);
        flContainer = (FrameLayout) findViewById(R.id.fl_container);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        SUtils.clickTransColor(llBack);
        viewBack = findViewById(R.id.view_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClick();
            }
        });
        line1 = findViewById(R.id.line1);
        rightTv = (TextView) findViewById(R.id.tv_edit);
    }

    public void changeViewBackRes(int res){
        if(viewBack == null){
            return;
        }
        viewBack.setBackgroundResource(res);
    }

    /**
     * 隐藏返回键
     */
    public void hideViewBack() {
        viewBack.setVisibility(View.GONE);
    }

    @Override
    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        if (titleId != 0)
            setTitle(getString(titleId));
    }

    protected void onBackClick() {

        CUtils.onClick(context, getClass().getSimpleName() + "_back");
        BaseRequestActivity.this.finish();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            CUtils.onClick(context, BACK_TAG + "_back");
        }
        return super.onKeyUp(keyCode, event);
    }

    public void initContentView() {
        if (setContentView() != 0) {
            View view = LayoutInflater.from(this).inflate(setContentView(), null);
            flContainer.addView(view);
        }
    }

    public void addView(View view){
        flContainer.addView(view);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void changeHeaderStyleTrans(int color) {
        if (rlTitle != null) {
            rlTitle.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                getWindow().setStatusBarColor(color);
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }
        }
    }

    public void requestData(Class className, SummerParameter params, final String url, boolean post) {
        baseHelper.setIsRefresh(isRefresh);
        baseHelper.requestData(0, className, params, url, post);
    }

    public void putData(int requestType, Class className, SummerParameter params, final String url) {
        baseHelper.setIsRefresh(isRefresh);
        baseHelper.putData(requestType, className, params, url);
    }

    public void requestData(int requestCode, Class className, SummerParameter params, final String url, boolean post) {
        requestData(requestCode, className, params, url, post, false);
    }

    public void requestData(int requestCode, Class className, SummerParameter params, final String url, boolean post, boolean isArray) {
        baseHelper.setIsRefresh(isRefresh);
        baseHelper.requestData(requestCode, 0, className, params, url, post, isArray);
    }

    @Override
    protected void onStop() {
        super.onStop();
      /*  if (baseHelper != null) {
            baseHelper.cancelLoading();
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        context = null;
        BitmapUtils.getInstance().clearBitmaps(getClass().getSimpleName());
    }

    /**
     * 设置沉浸式状态栏
     */
    public void setLayoutFullscreen() {
        baseHelper.setLayoutFullscreen(false);
    }

    /**
     * 设置沉浸式状态栏
     */
    public void setLayoutFullscreen(boolean show) {
        baseHelper.setLayoutFullscreen(show);
    }


    /**
     * 设置沉浸式状态栏
     *//*
    public void setLayoutFullscreen(boolean fullscreen) {
        baseHelper.setLayoutFullscreen(fullscreen);
    }*/

    public static class MyHandler extends Handler {
        private final WeakReference<BaseRequestActivity> mActivity;

        public MyHandler(BaseRequestActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseRequestActivity activity = mActivity.get();
            if (null != activity) {
                switch (msg.what) {
                    case BaseHelper.MSG_SUCCEED:
                        activity.handleRequest(msg, false);
                        break;
                    case BaseHelper.MSG_FINISHLOAD:
                        activity.cancelBaseLoading();
                        activity.finishLoad();
                        break;
                    case BaseHelper.MSG_CACHE:
                        activity.handleRequest(msg, true);
                        break;
                    case BaseHelper.MSG_ERRO:
                        Logs.i("requestCode:" + msg.arg1 + ",,," + msg.arg2);
                        activity.dealErrors(msg.arg1, msg.arg2 + "", (String) msg.obj, msg.arg2 > 0 ? true : false);
                        activity.finishLoad();
                        break;
                    case BaseHelper.MSG_LOADING:
                        String content = (String) msg.obj;
                        activity.startLoading(content);
                        break;
                    case BaseHelper.MSG_CANCEL_LOADING:
                        activity.cancelBaseLoading();
                        break;
                    default:
                        activity.handleMsg(msg.what, msg.obj);
                }
            }
        }
    }

    private void startLoading(String content) {
        LoadingDialog.cancelDialogForLoading();
        LoadingDialog.showDialogForLoading(this, content, true);
    }

    public void cancelLoading() {
        myHandlder.sendEmptyMessage(BaseHelper.MSG_CANCEL_LOADING);
    }

    public void showLoading() {
        myHandlder.obtainMessage(BaseHelper.MSG_LOADING, "正在加载中...").sendToTarget();
    }

    public void showLoading(String content) {
        myHandlder.obtainMessage(BaseHelper.MSG_LOADING, content).sendToTarget();
    }

    public void showOtherViewFromEmpty(int resLayout) {
        flContainer.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.view_empty, null);
        flContainer.addView(view);
    }

    /**
     * 处理MyHandler派发的消息
     *
     * @param position
     * @param object
     */
    public void handleMsg(int position, Object object) {

    }

    /**
     * 返回数据给请求Activity
     *
     * @param msg
     * @param fromCache
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void handleRequest(Message msg, boolean fromCache) {
        if (context == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                if (isDestroyed()) {
                    return;
                }
            } catch (NoSuchElementException e) {
                e.printStackTrace();
            }
        }

        Object object = msg.obj;
        if (object == null) {
            return;
        }
        if (object instanceof BaseResp) {
            BaseResp resp = (BaseResp) object;
            if (toastMessage && !isReturnFailureMsg) {
                //new CodeRespondUtils(context, resp);
            } else {
            }
            dealDatas(msg.arg1, msg.obj);
        }
        dealDatas(msg.arg1, msg.obj);
        if (fromCache) {
            fromId = 0;
            pageIndex = 0;
        }
    }

    /**
     * 处理错误
     *
     * @param requstCode  请求数据标识码
     * @param requestType 返回错误码，根据requestCode判断是返回的Code还是@ErroCode
     * @param errString   错误信息
     * @param isCode      如果是返回的Code则为true
     */
    protected void dealErrors(int requstCode, String requestType, String errString, boolean isCode) {
        if (baseHelper != null) {
            baseHelper.cancelLoading();
            finishLoad();
        }
    }

    private void cancelBaseLoading() {
        if (baseHelper != null) {
            baseHelper.cancelLoading();
        }
        LoadingDialog.cancelDialogForLoading();
    }

    /**
     * 显示空状态页面
     *
     * @param msg  显示文本信息
     * @param resp
     */
    protected void showEmptyView(String msg, int resp) {
        if(context == null){
            return;
        }
        flContainer.removeAllViews();
        View view = LayoutInflater.from(context).inflate(R.layout.view_empty, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*    if (baseHelper != null) {
                    baseHelper.setShowLoading(true);
                }*/
                //requestRankData();
            }
        });
        flContainer.addView(view);
        ImageView ivNav = view.findViewById(R.id.iv_nav);
        SUtils.setPicResource(ivNav, resp);

        TextView tvContent = (TextView) view.findViewById(R.id.tv_hint_content);
        if (!TextUtils.isEmpty(msg)) {
            tvContent.setText(msg);
        }
    }

    /**
     * 显示空状态页面
     *
     * @param msg  显示文本信息
     * @param resp
     */
    protected void showEmptyView(String msg, int resp, String reloadContent, View.OnClickListener listener) {
        flContainer.removeAllViews();
        View view = LayoutInflater.from(context).inflate(R.layout.view_empty, null);
        TextView tvReload = view.findViewById(R.id.tv_reload);
        tvReload.setText(reloadContent);
        tvReload.setVisibility(View.VISIBLE);
        tvReload.setOnClickListener(listener);
        flContainer.addView(view);
        ImageView ivNav = view.findViewById(R.id.iv_nav);
        SUtils.setPicResource(ivNav, resp);

        TextView tvContent = (TextView) view.findViewById(R.id.tv_hint_content);
        if (!TextUtils.isEmpty(msg)) {
            tvContent.setText(msg);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
     /*   MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(this.getClass().getName());*/
        // CUtils.onClick(context, BACK_TAG + "_resume");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
       /* MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(this.getClass().getName());*/
        //CUtils.onClick(context, BACK_TAG + "_pause");
        if (tvTitle != null) {
            SUtils.hideSoftInpuFromWindow(tvTitle);
        }
    }

    public void showKeyboard(final View view){
        myHandlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                SUtils.showSoftInpuFromWindow(view,context);
            }
        },300);
    }

    /**
     * 获取颜色资源
     *
     * @param coloRes
     * @return
     */
    public int getResColor(int coloRes) {
        if (resources == null) {
            resources = context.getResources();
        }
        return resources.getColor(coloRes);
    }

    public void setToastMessage() {
        this.toastMessage = true;
    }

    public boolean isReturnFailureMsg() {
        return isReturnFailureMsg;
    }

    public void setReturnFailureMsg(boolean returnFailureMsg) {
        isReturnFailureMsg = returnFailureMsg;
    }

    public void setToastMessage(boolean isShow) {
        this.toastMessage = isShow;
    }

    protected abstract void loadData();

    protected abstract void finishLoad();

    protected abstract int setTitleId();

    protected abstract int setContentView();

    //简单页面无需mvp就不用管此方法即可,完美兼容各种实际场景的变通
    public abstract void initPresenter();

    protected abstract void initData();

    protected abstract void dealDatas(int requestCode, Object obj);


}
