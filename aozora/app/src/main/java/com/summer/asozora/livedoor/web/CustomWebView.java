/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.summer.asozora.livedoor.web;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.balanx.nfhelper.utils.SUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * A convenient extension of WebView.
 */
public class CustomWebView extends WebView {

    private static final String TAG = "CustomWebView";

    private Context mContext;

    private int mProgress = 100;

    private boolean mIsLoading = false;

    private String mLoadedUrl;

    private String gameIcon;

    private String originalTitle;

    private CookieManager cookieManager = CookieManager.getInstance();

    /**
     * Constructor.
     *
     * @param context The current context.
     */
    public CustomWebView(Context context) {
        super(context);

        mContext = context;

        initializeOptions();

    }

    /**
     * Constructor.
     *
     * @param context The current context.
     * @param attrs   The attribute set.
     */
    public CustomWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        initializeOptions();
    }

    /**
     * Initialize the WebView with the options set by the user through
     * preferences.
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void initializeOptions() {
        WebSettings settings = getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setSaveFormData(true);
        settings.setSavePassword(true);
        CookieManager.getInstance().setAcceptCookie(true);
        // User settings
        settings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // Technical settings
        settings.setSupportZoom(false);
        settings.setSupportMultipleWindows(false);
        settings.setBuiltInZoomControls(false);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setGeolocationEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAppCacheMaxSize(Long.MAX_VALUE);
        settings.setAppCachePath(mContext.getDir("appcache", 0).getPath());
        settings.setDatabasePath(mContext.getDir("databases", 0).getPath());
        String userAgent = settings.getUserAgentString();
        settings.setUserAgentString("mac os");
        settings.setDefaultTextEncodingName("utf-8");
        settings.setBlockNetworkImage(false);//解决图片不显示
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        setLongClickable(true);
        setScrollbarFadingEnabled(true);
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        setDrawingCacheEnabled(true);
        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
        settings.setUseWideViewPort(true);
        settings.setDefaultFontSize(15);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP)
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
    }

    public void synCookies(Context context, String url, String cookiesKey, String cookies) {
        CookieSyncManager.createInstance(context).startSync();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, cookiesKey + "=" + cookies);// 指定要修改的cookies
        cookieManager.setCookie(url, "TOKEN" + "=" +   SUtils.getStringData(context, "TOKEN"));// 指定要修改的cookies
        CookieSyncManager.getInstance().sync();
    }

    public static void removeCookie() {
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean value) {
                }
            });
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean value) {
                }
            });
        }else {
            cookieManager.removeSessionCookie();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setCookies(final Context context, String url, String token) {
        removeCookie();
        synCookies(context, url, "SESSION", token);
    }

    @Override
    public void loadUrl(String url) {
        try {
            // 所有网页请求都加入请求头
            Map<String, String> additionalHttpHeaders = new HashMap<String, String>();
            super.loadUrl(url, additionalHttpHeaders);
        } catch (NullPointerException e) {
        }
    }

    /**
     * Set the current loading progress of this view.
     *
     * @param progress The current loading progress.
     */
    public void setProgress(int progress) {
        mProgress = progress;
    }

    /**
     * Get the current loading progress of the view.
     *
     * @return The current loading progress of the view.
     */
    public int getProgress() {
        return mProgress;
    }

    /**
     * Triggered when a new page loading is requested.
     */
    public void notifyPageStarted() {
        mIsLoading = true;
    }

    /**
     * Triggered when the page has finished loading.
     */
    public void notifyPageFinished() {
        mProgress = 100;
        mIsLoading = false;
    }

    /**
     * Check if the view is currently loading.
     *
     * @return True if the view is currently loading.
     */
    public boolean isLoading() {
        return mIsLoading;
    }

    /**
     * Get the loaded url, e.g. the one asked by the user, without redirections.
     *
     * @return The loaded url.
     */
    public String getLoadedUrl() {
        return mLoadedUrl;
    }

    public void setLoadedUrl(String url) {
        this.mLoadedUrl = url;
    }

    /**
     * Reset the loaded url.
     */
    public void resetLoadedUrl() {
        mLoadedUrl = null;
    }

    public boolean isSameUrl(String url) {
        if (url != null) {
            return url.equalsIgnoreCase(this.getUrl());
        }

        return false;
    }

    public void destoryWebView() {
        onPause();
        setVisibility(View.GONE);
    }

    public void destoryCurrentWebView() {
        onPause();
        destroy();
        removeAllViews();
    }

    public String getGameIcon() {
        return gameIcon;
    }

    public void setGameIcon(String gameIcon) {
        this.gameIcon = gameIcon;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
    }


    public interface ScrollInterface {
        void onSChanged(CustomWebView mWebView, View toolBarView, int l, int t,
                        int oldl, int oldt);
    }

}
