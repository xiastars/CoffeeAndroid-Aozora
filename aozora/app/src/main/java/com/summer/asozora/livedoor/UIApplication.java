package com.summer.asozora.livedoor;

import android.app.Application;
import android.content.Context;
import cn.bmob.v3.Bmob;

public class UIApplication extends Application {
	
	public static Context context;

	@Override
	public void onCreate() {
		super.onCreate();
		context = UIApplication.this;
		Bmob.initialize(this, "8972c8b2028d9eed130bf708bbd713a5");
		
	}

	public static Context getAppContext() {
		return context;
	}
}
