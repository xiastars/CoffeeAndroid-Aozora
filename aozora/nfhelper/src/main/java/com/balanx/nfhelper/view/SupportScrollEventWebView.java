package com.balanx.nfhelper.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebView;
import android.widget.ZoomButtonsController;

import com.balanx.nfhelper.listener.OnSimpleClickListener;

import java.lang.reflect.Method;

/**
 * Created by xiastars on 2017/8/29.
 */

public class SupportScrollEventWebView extends WebView {
    private ZoomButtonsController zoomController = null;
    private OnSimpleClickListener onSingleTabListener;
    private GestureDetector doubleTapDetecture;

    public SupportScrollEventWebView(Context context) {
        super(context);
        disableZoomController(context);
    }

    public SupportScrollEventWebView(Context context, AttributeSet attrs,
                                     int defStyle) {
        super(context, attrs, defStyle);
        disableZoomController(context);
    }

    public SupportScrollEventWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        disableZoomController(context);
    }

    private void disableZoomController(Context context) {
        doubleTapDetecture = new GestureDetector(context, new GestureListener());
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            this.getSettings().setBuiltInZoomControls(true);
            this.getSettings().setDisplayZoomControls(false);
        } else {
            getControlls();
        }
    }

    private void getControlls() {
        try {
            Class webview = Class.forName("android.webkit.WebView");
            Method method = webview.getMethod("getZoomButtonsController");
            zoomController = (ZoomButtonsController) method.invoke(this, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        doubleTapDetecture.onTouchEvent(ev);
        if (zoomController != null) {
            // Hide the controlls AFTER they where made visible by the default
            // implementation.
            zoomController.setVisible(false);
        }
        return true;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if(onSingleTabListener!=null)onSingleTabListener.onClick(0);
            return super.onSingleTapConfirmed(e);
        }
    }

    public void setOnSingleTabListener(final OnSimpleClickListener onSingleTabListener){
        this.onSingleTabListener = onSingleTabListener;
    }

}