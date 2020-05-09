package com.summer.asozora.livedoor;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

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
