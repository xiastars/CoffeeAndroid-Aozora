package com.summer.app.wuteai.utils;

import java.io.Serializable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * 跳转方法
 * 
 * @author dingzai_xiaqiliang
 * @Time 2014-11-28
 */
public class JumpTo {

	private static JumpTo jumpTo = null;
	public static String TYPE_LONG = "tag_long";
	public static String TYPE_INT = "tag_int";
	public static String TYPE_OBJECT = "tag_object";
	public static String TYPE_STRING = "tag_string";

	public static synchronized JumpTo getInstance() {
		if (jumpTo == null) {
			jumpTo = new JumpTo();
		}
		return jumpTo;
	}
	
	public class ShortcutJump{
		public static final String TYPE_URL = "jump_url";
		public static final String TYPE_LOGO = "jump_logo";
		public static final String TYPE_NAME = "jump_name";
	}

	/**
	 * 普通的跳转方法，不需要传递数据
	 * 
	 * @param context
	 * @param cls
	 */
	public void commonJump(Context context, Class<?> cls) {
		Intent intent = new Intent(context, cls);
		context.startActivity(intent);
	}

	/**
	 * 普通的跳转方法，不需要传递数据 可回调
	 * 
	 * @param context
	 * @param cls
	 */
	public void commonResultJump(Context context, Class<?> cls, int requestCode) {
		Intent intent = new Intent(context, cls);
		((Activity) context).startActivityForResult(intent, requestCode);
	}

	/**
	 * 普通的跳转方法，带一个标识符
	 * 
	 * @param context
	 * @param cls
	 * @param tag
	 */
	public void commonJump(Context context, Class<?> cls, long tag, int tag2) {
		Intent intent = new Intent(context, cls);
		intent.putExtra(TYPE_INT, tag2);
		intent.putExtra(TYPE_LONG, tag);
		context.startActivity(intent);
	}
	
	/**
	 * 普通的跳转方法，带一个标识符
	 * 
	 * @param context
	 * @param cls
	 * @param tag
	 */
	public void commonJump(Context context, Class<?> cls, long tag, String tag2) {
		Intent intent = new Intent(context, cls);
		intent.putExtra(TYPE_LONG, tag);
		intent.putExtra(TYPE_STRING, tag2);
		context.startActivity(intent);
	}
	
	/**
	 * 普通的跳转方法，带一个标识符和回调
	 * 
	 * @param context
	 * @param cls
	 * @param tag
	 */
	public void commonResultJump(Context context, Class<?> cls, long tag, int tag2, int requestCode) {
		Intent intent = new Intent(context, cls);
		intent.putExtra(TYPE_INT, tag2);
		intent.putExtra(TYPE_LONG, tag);
		((Activity) context).startActivityForResult(intent, requestCode);
	}

	/**
	 * 单个long类型
	 * 
	 * @param context
	 * @param cls
	 * @param tag
	 */
	public void commonJump(Context context, Class<?> cls, long tag) {
		Intent intent = new Intent(context, cls);
		intent.putExtra(TYPE_LONG, tag);
		context.startActivity(intent);
	}

	/**
	 * 单个int类型
	 * 
	 * @param context
	 * @param tag2
	 */
	public void commonJump(Context context, Class<?> cls, int tag2) {
		Intent intent = new Intent(context, cls);
		intent.putExtra(TYPE_INT, tag2);
		context.startActivity(intent);
	}

	/**
	 * String 类型
	 * 
	 * @param context
	 * @param cls
	 * @param tag
	 */
	public void commonJump(Context context, Class<?> cls, String tag) {
		Intent intent = new Intent(context, cls);
		intent.putExtra(TYPE_STRING, tag);
		context.startActivity(intent);
	}

	/**
	 * 简单的获取Long参数的方法
	 * 
	 * @param context
	 */
	public static long getLong(Activity context) {
		return context.getIntent().getLongExtra(TYPE_LONG, 0);
	}

	/**
	 * 简单的获取String参数的方法
	 * 
	 * @param context
	 */
	public static String getString(Activity context) {
		return context.getIntent().getStringExtra(TYPE_STRING);
	}

	/**
	 * 简单的获取int参数的方法
	 * 
	 * @param context
	 */
	public static int getInteger(Activity context) {
		return context.getIntent().getIntExtra(TYPE_INT, 0);
	}
	
	/**
	 * 获取Shortcut链接
	 * @param context
	 * @return
	 */
	public static String getShortcutUrl(Activity context) {
		return context.getIntent().getStringExtra(ShortcutJump.TYPE_URL);
	}
	
	public static String getShortcutName(Activity context) {
		return context.getIntent().getStringExtra(ShortcutJump.TYPE_NAME);
	}
	
	public static String getShortcutLogo(Activity context) {
		return context.getIntent().getStringExtra(ShortcutJump.TYPE_LOGO);
	}

	public void commonJump(Context context, Class<?> cls, Object object) {
		Intent intent = new Intent(context, cls);
		intent.putExtra(TYPE_OBJECT, (Serializable) object);
		context.startActivity(intent);
	}

	/**
	 * 简单的获取Object参数的方法
	 * 
	 * @param context
	 */
	public static Object getObject(Activity context) {
		return context.getIntent().getSerializableExtra(TYPE_OBJECT);
	}

}
