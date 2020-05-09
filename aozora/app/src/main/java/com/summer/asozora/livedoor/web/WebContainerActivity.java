package com.summer.asozora.livedoor.web;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.balanx.nfhelper.utils.JumpTo;
import com.balanx.nfhelper.utils.Logs;
import com.summer.asozora.livedoor.R;

public class WebContainerActivity extends Activity{

    private WebContainerActivity INSTANCE;
    FrameLayout llContainerLayout;

    private String loadPageUrl;
    public String title;
    public int isFromMain = 0;
    private boolean isHomeKey = false;
    public WebTopBarManager webTopBarManager;
    ProgressBar mProgressBar;
    TextView tvTitle;

    boolean loadError;//加载失败

    public static void show(Context context,String url,String title){
        Intent intent = new Intent(context, WebContainerActivity.class);
        intent.putExtra(JumpTo.TYPE_STRING, url);
        intent.putExtra("key_title", title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_container);
        INSTANCE = WebContainerActivity.this;
        WebContainerJumpView.initLocalActivityManager(this, savedInstanceState);
        initView();
        webTopBarManager = new WebTopBarManager(this);
        initData();
    }

    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        llContainerLayout = (FrameLayout) findViewById(R.id.container_layout);
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logs.i("xia", "ONRESUME");
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isHomeKey && isFromMain == 1) {
            isHomeKey = false;
            finish();
        }
    }

    private void initData() {
        loadPageUrl = JumpTo.getString(this);
        WebContainerJumpView.jumpToView(llContainerLayout, this, loadPageUrl);
        String title = getIntent().getStringExtra("key_title");
        if (title != null) {
            tvTitle.setText(title);
        }
        findViewById(R.id.ll_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigatePrevious();
            }
        });
    }

    class HomeKeyEventBroadCastReceiver extends BroadcastReceiver {

        static final String SYSTEM_REASON = "reason";
        static final String SYSTEM_HOME_KEY = "homekey";// home key
        static final String SYSTEM_RECENT_APPS = "recentapps";// long home key

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_REASON);
                if (reason != null) {
                    //Home键清除webView
                    if (reason.equals(SYSTEM_HOME_KEY)) {
                        isHomeKey = true;
                    } else if (reason.equals(SYSTEM_RECENT_APPS)) {
                        // long home key处理点
                    }
                }
            }
        }
    }

    //
    private void navigatePrevious() {
        if (webTopBarManager.mCurrentWebView.canGoBack() && !loadError) {
            webTopBarManager.mCurrentWebView.goBack();
        } else {
            ActivitysManager.finish("WebContainerActivity");
            finish();
            isFromMain = 0;
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN
                && event.getRepeatCount() == 0 && !loadError) {
            navigatePrevious();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public boolean isLoadError() {
        return loadError;
    }

    public void setLoadError(boolean loadError) {
        this.loadError = loadError;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
