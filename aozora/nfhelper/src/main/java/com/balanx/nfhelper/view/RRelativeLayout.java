package com.balanx.nfhelper.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.balanx.nfhelper.utils.SUtils;

/**
 * Created by xiaqiliang on 2017/3/28.
 */
public class RRelativeLayout extends RelativeLayout {

    public RRelativeLayout(Context context) {
        super(context);
    }

    public RRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void reLayout(int width, int height, int marginLeft, int marginTop) {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (width != 0) {
            params.width = SUtils.getSWidth((Activity) getContext(), width);
        }
        if (height != 0) {
            params.height = SUtils.getSHeight((Activity) getContext(), height);
        }
        if (params instanceof LayoutParams) {
            if (marginLeft != 0) {
                ((LayoutParams) params).leftMargin = SUtils.getSWidth((Activity) getContext(), marginLeft);
            }
            if (marginTop != 0) {
                ((LayoutParams) params).topMargin = SUtils.getSHeight((Activity) getContext(), marginTop);
            }
        } else if (params instanceof LinearLayout.LayoutParams) {
            if (marginLeft != 0) {
                ((LinearLayout.LayoutParams) params).leftMargin = SUtils.getSWidth((Activity) getContext(), marginLeft);
            }
            if (marginTop != 0) {
                ((LinearLayout.LayoutParams) params).topMargin = SUtils.getSHeight((Activity) getContext(), marginTop);
            }
        } else if (params instanceof FrameLayout.LayoutParams) {
            if (marginLeft != 0) {
                ((FrameLayout.LayoutParams) params).leftMargin = SUtils.getSWidth((Activity) getContext(), marginLeft);
            }
            if (marginTop != 0) {
                ((FrameLayout.LayoutParams) params).topMargin = SUtils.getSHeight((Activity) getContext(), marginTop);
            }
        }
    }

    public void reLayoutOrigin(int width, int height) {
        ViewGroup.LayoutParams params = getLayoutParams();
        if (width != 0) {
            params.width = width;
        }
        if (height != 0) {
            params.height = height;

        }
    }
}
