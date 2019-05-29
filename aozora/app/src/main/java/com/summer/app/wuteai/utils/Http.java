package com.summer.app.wuteai.utils;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import android.graphics.Bitmap;
import android.widget.ImageView;
import okhttp3.Call;

public final class Http {
	

    public static void setImage(final ImageView view,String url)
    {
        OkHttpUtils
                .get()//
                .url(url)//
                .build()//
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(new BitmapCallback()
                {
                    @Override
                    public void onError(Call call, Exception e)
                    {
                    }

                    @Override
                    public void onResponse(Bitmap bitmap)
                    {
                    	view.setImageBitmap(bitmap);
                    }
                });
    }
	
}
