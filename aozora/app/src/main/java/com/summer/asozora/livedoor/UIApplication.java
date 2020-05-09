package com.summer.asozora.livedoor;

import android.content.Context;

import com.summer.asozora.livedoor.base.BaseApplication;

public class UIApplication extends BaseApplication {

	public static boolean DEBUGMODE = true;
	public static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		context = UIApplication.this;
		//Bmob.initialize(this, "8972c8b2028d9eed130bf708bbd713a5");
		
	}

	public static Context getAppContext() {
		return context;
	}
}
