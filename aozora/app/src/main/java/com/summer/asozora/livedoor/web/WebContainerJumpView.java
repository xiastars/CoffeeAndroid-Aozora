package com.summer.asozora.livedoor.web;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.balanx.nfhelper.utils.JumpTo;

import java.util.HashMap;
import java.util.Map;

public class WebContainerJumpView {

    private static Map<String, View> viewMaps = new HashMap<String, View>();

    public static void add(String activityName, View view) {
        viewMaps.put(activityName, view);
    }

    public static View getView(String id) {
        View r = viewMaps.get(id);
        return r != null ? r : null;
    }

    public static void removeView(String id) {
        viewMaps.remove(id);
    }

    public static void clear() {
        viewMaps.clear();
    }

    public static LocalActivityManager manager = null;

    public static void initLocalActivityManager(Activity activity, Bundle savedInstanceState) {
        manager = new LocalActivityManager(activity, true);
        manager.dispatchCreate(savedInstanceState);
    }

    public static void jumpToView(FrameLayout llContainerLayout, WebContainerActivity activity, String loadPageUrl) {
        llContainerLayout.removeAllViews();
        View view = WebContainerJumpView.getView("WebViewActivity_" + loadPageUrl);
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(JumpTo.TYPE_STRING, loadPageUrl);
        intent.putExtra(JumpTo.ShortcutJump.TYPE_URL, activity.getIntent().getStringExtra(JumpTo.ShortcutJump.TYPE_URL));
        view = getView(manager, loadPageUrl, intent);
        if (viewMaps != null && viewMaps.size() <= 5) {
            WebContainerJumpView.add("WebViewActivity_" + loadPageUrl, view);
        }
        View parent = (View) view.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(view);
        }
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(view);
        llContainerLayout.addView(layout);
    }

    public static View getView(LocalActivityManager manager, String id, Intent intent) {
        return manager.startActivity(id, intent).getDecorView();
    }
}
