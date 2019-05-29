package com.summer.asozora.livedoor;

import com.summer.app.wuteai.utils.SUtils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class BaseActivity extends Activity{
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setRequestedOrientation((ActivityInfo.SCREEN_ORIENTATION_PORTRAIT));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	public void setTitle(String title){
		this.title = title;
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {

			
		}
	}

}
