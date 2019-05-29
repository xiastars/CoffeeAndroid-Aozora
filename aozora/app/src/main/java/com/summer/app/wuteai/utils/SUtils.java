package com.summer.app.wuteai.utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SUtils {
	
	public static int screenHeight;
	public static int screenWidth;
	private static InputMethodManager inputMethodManager = null;

	/**
	 * 弹出提示
	 * @param context
	 * @param id
	 */
	public static void makeToast(Context context,int id){
		Toast.makeText(context, id, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 弹出提示
	 * @param context
	 * @param text
	 */
	public static void makeToast(Context context,String text){
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 设置不为空的TextView
	 * @param view
	 * @param text
	 */
	public static void setNotEmptText(TextView view ,String text){
		if(!TextUtils.isEmpty(text)){
			view.setText(text);
		}else{
			view.setText("");
		}
	}
	
	
	/**
	 * 获取int类型数据
	 * @param mContext
	 * @param selected
	 */
	public static void saveIntegerData(Context mContext,String type, int selected) {
		SharedPreferences settings = mContext.getSharedPreferences(
				"savedata", 0);
		settings.edit().putInt(type, selected)
				.commit();
	}

	/**
	 * 得到int类型数据
	 * @param mContext
	 * @return
	 */
	public static int getIntegerData(Context mContext,String type) {
		SharedPreferences settings = mContext.getSharedPreferences(
				"savedata", 0);
		int select = settings.getInt(type, 0);
		return select;
	}
	
	/**
	 * 获取boolean类型数据
	 * @param mContext
	 * @param selected
	 */
	public static void saveBooleanData(Context mContext,String type, Boolean selected) {
		SharedPreferences settings = mContext.getSharedPreferences(
				"savedata", 0);
		settings.edit().putBoolean(type, selected).commit();
	}

	/**
	 * 得到boolean类型数据
	 * @param mContext
	 * @return
	 */
	public static Boolean getBooleanData(Context mContext,String type) {
		SharedPreferences settings = mContext.getSharedPreferences(
				"savedata", 0);
		Boolean select = settings.getBoolean(type, false);
		return select;
	}
	
	/**
	 * 获取boolean类型数据
	 * @param mContext
	 * @param selected
	 */
	public static void saveStringData(Context mContext,String type, String selected) {
		SharedPreferences settings = mContext.getSharedPreferences(
				"savedata", 0);
		settings.edit().putString(type, selected).commit();
	}

	/**
	 * 得到boolean类型数据
	 * @param mContext
	 * @return
	 */
	public static String getStringData(Context mContext,String type) {
		SharedPreferences settings = mContext.getSharedPreferences(
				"savedata", 0);
		String select = settings.getString(type, null);
		return select;
	}
	
	/**
	 * 返回是否为当天，用于一些一天内只需请求一次的请求
	 * 
	 * @param context
	 * @return true 为是今天
	 */
	public static boolean isToday(Context context) {
		Calendar ca = Calendar.getInstance();
		String lastDay = SUtils.getStringData(context, "isToday");
		if (!lastDay.equals(ca.get(Calendar.MONTH) + "-"
				+ ca.get(Calendar.DATE) + "")) {
			return false;
		}
		return true;
	}
	
    /**
     * 保存今天日期
     * @param context
     */
	public static void saveToday(Context context) {
		Calendar ca = Calendar.getInstance();
		SUtils.saveStringData(context, "isToday", ca.get(Calendar.MONTH) + "-"
				+ ca.get(Calendar.DATE));
	}
	
	/**
	 * 为EditText设置光标
	 */
	public static void setSelection(EditText editView){
	           editView.setSelection(editView.getText().length());
	           editView.requestFocus();
	}
	
	/**
	 * xml里是dp，但是到代码里是px，这个方法让传进去的dp仍然保持dp
	 * 
	 * @param context
	 * @param value
	 *            传的dp值
	 * @return
	 */
	public static int getDip(Context context, int value) {
		int pageMargin = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, value, context.getResources()
						.getDisplayMetrics());
		return pageMargin;
	}
	
	public static float getDip(Context context, float value) {
		float pageMargin = TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, value, context.getResources()
						.getDisplayMetrics());
		return pageMargin;
	}
	
	/**
	 * 初始化当前屏幕分辨率
	 */
	public static void initScreenDisplayMetrics(Activity context) {
		DisplayMetrics metric = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metric);
		SUtils.screenHeight = metric.heightPixels;
		SUtils.screenWidth = metric.widthPixels;
	}
	
	
	/**
	 * 隐藏输入法
	 */
	public static void hideSoftInpuFromWindow(EditText searchEdit,
			Context context) {
		if (searchEdit == null)
			return;
		if (inputMethodManager == null)
			inputMethodManager = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(searchEdit.getWindowToken(),
				0);
	}

	/**
	 * 打开输入法
	 */
	public static void showSoftInpuFromWindow(EditText searchEdit,
			Context context) {
		if (searchEdit == null)
			return;
		if (searchEdit != null) {
			searchEdit.requestFocus();
		}
		if (inputMethodManager == null)
			inputMethodManager = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.showSoftInput(searchEdit,
				InputMethodManager.SHOW_FORCED);
	}
	
	/**
	 * 判断现在网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 设置支持表情的Text
	 * @param name
	 * @param showName
	 * @param context
	 */
	public static void setHtmlText(String name,TextView showName){
		showName.setText(Html.fromHtml(name), null);	
	}
	
	/**
	 * 将当前时间格式化为 --年--月--日
	 * @return
	 */
	public static String getDays(Date date){
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日",Locale.CHINA);
		String s = format.format(date);
		return s;
	}
	
	/**
	 * 将中国格式的时间转化为毫秒
	 * @param fString
	 * @return
	 * @throws ParseException
	 */
	public static long getTime(String fString) throws ParseException{
		long mTime = 0;
		SimpleDateFormat format = new SimpleDateFormat("yyyy"+"-"+"MM"+"-"+"dd"+" "+"hh"+":"+"mm"+":"+"ss",Locale.CHINA); 
		mTime = format.parse(fString).getTime();
	    return mTime;			
	}
	
	/**
 	 * TextView 点击改变颜色,使其透明化
 	 * @param view
 	 */
 	public static void clickTransColor(final TextView view){
 		final ColorStateList color = view.getTextColors();
 		view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					view.setTextColor(color.withAlpha(100));
					view.setScaleX(1.2f);
					view.setScaleY(1.2f);
					break;
				case MotionEvent.ACTION_UP:
					view.setAlpha(255);
					view.setTextColor(color);
					view.setScaleX(1f);
					view.setScaleY(1f);
					break;
				}
				return false;
			}
		});
 	}
 	
 	/**
 	 * TextView 点击改变颜色,使其透明化
 	 * @param view
 	 */
 	public static void clickTransColor(final ImageView view){
 		view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
//					view.getBackground().setAlpha(100);
					view.setScaleX(1.2f);
					view.setScaleY(1.2f);
					break;
				case MotionEvent.ACTION_UP:
//					view.getBackground().setAlpha(255);
					view.setScaleX(1f);
					view.setScaleY(1f);
					break;
				}
				return false;
			}
		});
 	}
 	
 	/**** 取SD卡路径不带/ ****/
	public static String getSDPath() {
		File sdDir = null;
		try {
			boolean sdCardExist = android.os.Environment
					.getExternalStorageState().equals(
							android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
			if (sdCardExist) {
				sdDir = android.os.Environment
						.getExternalStorageDirectory();// 获取跟目录
			} else {
				File file = new File(Environment.getDataDirectory()
						+ "/sdcard");
				if (file.canRead()) {
					return file.toString();
				} else {
					return "";
				}
			}
			if (sdDir != null) {
				return sdDir.toString();
			}
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
		}
		return "";
	}
	
	public static int getStatusBarHeight(Activity activity){
		int statusHeight = 0;  
	    Rect localRect = new Rect();  
	    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);  
	    statusHeight = localRect.top;  
	    if (0 == statusHeight){  
	        Class<?> localClass;  
	        try {  
	            localClass = Class.forName("com.android.internal.R$dimen");  
	            Object localObject = localClass.newInstance();  
	            int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());  
	            statusHeight = activity.getResources().getDimensionPixelSize(i5);  
	        } catch (ClassNotFoundException e) {  
	            e.printStackTrace();  
	        } catch (IllegalAccessException e) {  
	            e.printStackTrace();  
	        } catch (InstantiationException e) {  
	            e.printStackTrace();  
	        } catch (NumberFormatException e) {  
	            e.printStackTrace();  
	        } catch (IllegalArgumentException e) {  
	            e.printStackTrace();  
	        } catch (SecurityException e) {  
	            e.printStackTrace();  
	        } catch (NoSuchFieldException e) {  
	            e.printStackTrace();  
	        }  
	    }  
	    return statusHeight;  
	}

}
