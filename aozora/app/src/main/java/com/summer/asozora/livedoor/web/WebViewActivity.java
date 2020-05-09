package com.summer.asozora.livedoor.web;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.balanx.nfhelper.utils.JumpTo;
import com.balanx.nfhelper.utils.Logs;
import com.balanx.nfhelper.view.RoundAngleImageView;

public class WebViewActivity extends Activity {

    private WebViewActivity INSTANCE;

    RoundAngleImageView ivLoadingIcon;
    CustomWebView mCurrentWebView;
    LinearLayout loadingContainer;

    public WebContainerActivity activity;

    private String loadPageUrl;
    private String title;
    private boolean isLoadingEnd = false;
    boolean loadError;//加载失败

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.balanx.nfhelper.R.layout.activity_webview);
        INSTANCE = WebViewActivity.this;
        activity = (WebContainerActivity) getParent();
        initView();
        initializeCurrentWebView();
        setData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        loadingContainer = (LinearLayout) findViewById(com.balanx.nfhelper.R.id.loading_container);
        mCurrentWebView = (CustomWebView) findViewById(com.balanx.nfhelper.R.id.webview_container);
        ivLoadingIcon = (RoundAngleImageView) findViewById(com.balanx.nfhelper.R.id.iv_loading_icon);
        activity.webTopBarManager.setCurrentWebView(mCurrentWebView);
    }

    /**
     * 其它应用跳转监听
     */
    private void setData() {

        Intent i = getIntent();
        title = getIntent().getStringExtra("key_title");
        int tag = getIntent().getIntExtra(JumpTo.TYPE_INT, 0);
        loadPageUrl = JumpTo.getString(INSTANCE);
        navigateToUrl(loadPageUrl);
        if (!TextUtils.isEmpty(loadPageUrl)) {
            getCurrentWebView().setLoadedUrl(loadPageUrl);
        }
        ActivitysManager.Add("WebViewActivity_" + loadPageUrl, WebViewActivity.this);
        if (!TextUtils.isEmpty(title)) {
            this.getCurrentWebView().setOriginalTitle(title);
        }

    }

    /**
     * 设置跳转Url显示路径
     */
    public void navigateToUrl(String url) {
        if ((url != null) && (url.length() > 0)) {
//			if (UrlUtils.isUrl(url)) {
//				url = UrlUtils.checkUrl(url);
//			} else {
//				url = UrlUtils.getSearchUrl(this, url);
//			}
            mCurrentWebView.loadUrl(url);
        }
    }

    public void onPageStarted(String url) {
        this.loadPageUrl = url;
        activity.getProgressBar().setVisibility(View.VISIBLE);
    }

    public void onPageFinished(String url) {
//		mSwipeLayout.setRefreshing(false);
        this.loadPageUrl = url;
        mHandler.removeMessages(0);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(0), 700);
        activity.getProgressBar().setVisibility(View.GONE);
        setLoadingGone();
    }

    private void setLoadingVisible() {
        if (isLoadingEnd) return;
        loadingContainer.setVisibility(View.VISIBLE);
        ivLoadingIcon.clearAnimation();
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            requestUrlComment(loadPageUrl);
        }
    };

    public void setToolBarVisible(boolean isVisible) {
        mCurrentWebView.setVisibility(View.VISIBLE);
    }


    @SuppressLint("NewApi")
    private void setLoadingGone() {
//		activity.webTopBarManager.getIconLayout().setVisibility(View.VISIBLE);
        ivLoadingIcon.clearAnimation();
//		SUtils.setPic(ivImage, mDefaultIcon, true, new SimpleTarget() {
//
//			@Override
//			public void onResourceReady(Object arg0, GlideAnimation arg1) {
//				if (arg0 != null && arg0 instanceof GlideBitmapDrawable) {
//					Bitmap bitmap = ((GlideBitmapDrawable) arg0).getBitmap();
//					if (!bitmap.isRecycled()) {
//						ivImage.setImageBitmap(bitmap);
//						invalidate();
//						requestLayout();
//					}
//				}
//			}
//		});
        ivLoadingIcon.animate().scaleX(2.0f).scaleY(2.0f).setListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                loadingContainer.setVisibility(View.GONE);
                ivLoadingIcon.clearAnimation();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }
        }).start();
        isLoadingEnd = true;
    }

    /**
     * 初始化当前WebView
     */
    public void initializeCurrentWebView() {
        mCurrentWebView.setWebViewClient(new CustomWebViewClient(INSTANCE));
        activity.webTopBarManager.setCurrentWebView(mCurrentWebView);
        mCurrentWebView.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(final String url,
                                        final String userAgent, final String contentDisposition,
                                        final String mimetype, final long contentLength) {

            }

        });
        activity.webTopBarManager.setCurrentWebView(mCurrentWebView);
    }

    class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(
                    android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }

    }

    private void navigatePrevious() {
        activity.webTopBarManager.getProgressBar().setVisibility(View.GONE);
        if (mCurrentWebView.canGoBack()) {
            mCurrentWebView.goBack();
        }
    }

    public CustomWebView getCurrentWebView() {
        return mCurrentWebView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onExternalApplicationUrl(String url) {
        Logs.i("xia", "----------------------离线");
    }


    public void setUrlTag(String newUrl) {
    }

    private long lastTime = System.currentTimeMillis();

    public void requestUrlComment(String newUrl) {
        if (TextUtils.isEmpty(newUrl)) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime > 500) {
            activity.webTopBarManager.getCurrentWebView().setLoadedUrl(newUrl);
        }
        lastTime = currentTime;
    }

    public boolean isLoadError() {
        return loadError;
    }

    public void setLoadError(boolean loadError) {
        this.loadError = loadError;
        activity.setLoadError(loadError);
    }
}
