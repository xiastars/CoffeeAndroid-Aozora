package com.ferris.browser.view;
import com.summer.asozora.livedoor.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

/**
 * 
 * @ferris Administrator
 * 
 */
public class BrowserHomeView extends FrameLayout {

	public BrowserHomeView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public BrowserHomeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public BrowserHomeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
	}

	public static BrowserHomeView fromXml(Context context) {
		BrowserHomeView browserHomeView = (BrowserHomeView) LayoutInflater
				.from(context).inflate(R.layout.browser_home_view, null);
		return browserHomeView;
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
	}
}
