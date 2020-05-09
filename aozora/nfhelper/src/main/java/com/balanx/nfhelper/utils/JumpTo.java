package com.balanx.nfhelper.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

/**
 * 跳转方法，不需要传递Bundle使用commonJump(),如果传递内容以下方法不满足，请自定义
 * 所有跳转尽量写在这里
 *
 * @author xiaqiliang
 * @time 2016年6月7日
 */
public class JumpTo {

    private static JumpTo jumpTo = null;
    public static String TYPE_LONG = "tag_long";
    public static String TYPE_INT = "tag_int";
    public static String TYPE_INT2 = "tag_int2";
    public static String TYPE_OBJECT = "key";
    public static String TYPE_OBJECT2 = "key2";
    public static String TYPE_STRING = "tag_string";
    public static String TYPE_STRING2 = "tag_string2";
    public static String TYPE_BOOLEAN = "tag_boolean";
    public static String TYPE_BOOLEAN2 = "tag_boolean2";
    public static String TYPE_BOOLEAN3 = "tag_boolean3";

    public static synchronized JumpTo getInstance() {
        if (jumpTo == null) {
            jumpTo = new JumpTo();
        }
        return jumpTo;
    }

    public class ShortcutJump {
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
        startActivity(intent, (Activity) context);
    }

    /**
     * 渐进动画跳转
     *
     * @param intent
     * @param activity
     */
    boolean fortbitJumpt;

    public void startActivity(Intent intent, Activity activity) {
   /*     ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
   */
        //activity.startActivity(intent);
        activity.startActivity(intent);
    }

    /**
     * 普通的跳转方法，不需要传递数据
     *
     * @param context
     * @param cls
     */
    public void commonJump(Context context, ComponentName cls) {
        Intent intent = new Intent();
        intent.setClassName(cls.getPackageName(), cls.getClassName());
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
     * 普通的跳转方法，传object
     *
     * @param context
     * @param cls
     */
    public void commonResultJump(Context context, Class<?> cls, Object object, int requestCode) {
        Intent intent = new Intent(context, cls);
        if (object != null) {
            intent.putExtra(JumpTo.TYPE_OBJECT, (Serializable) object);
        }
        ((Activity) context).startActivityForResult(intent, requestCode);
    }

    public void commonResultJump(Context context, Class<?> cls, int value, int requestCode) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(JumpTo.TYPE_INT, value);
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

    public void commonResultJump(Context context, Class<?> cls, Serializable value1, int value2, int requestCode) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_OBJECT, value1);
        intent.putExtra(TYPE_INT, value2);
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
     * @param tag
     */
    public void commonJump(Context context, Class<?> cls, int tag) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_INT, tag);
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
        startActivity(intent, (Activity) context);
    }

    /**
     * String 类型
     *
     * @param context
     * @param cls
     * @param tag
     */
    public void commonJump(Context context, Class<?> cls, String tag, int inttag) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_STRING, tag);
        intent.putExtra(TYPE_INT, inttag);
        startActivity(intent, (Activity) context);
    }

    /**
     * String 类型
     *
     * @param context
     * @param cls
     * @param tag
     */
    public void commonJump(Context context, Class<?> cls, String tag, int inttag, String tag2) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_STRING, tag);
        intent.putExtra(TYPE_STRING2, tag2);
        intent.putExtra(TYPE_INT, inttag);
        startActivity(intent, (Activity) context);
    }

    /**
     * String 类型 + Object类型
     *
     * @param context
     * @param cls
     * @param tag
     */
    public void commonJump(Context context, Class<?> cls, String tag, String tag2) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_STRING, tag);
        intent.putExtra(TYPE_STRING2, tag2);
        startActivity(intent, (Activity) context);
    }

    /**
     * String 类型 + Object类型
     *
     * @param context
     * @param cls
     * @param tag
     */
    public void commonJump(Context context, Class<?> cls, Object object, String tag) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_STRING, tag);
        intent.putExtra(TYPE_OBJECT, (Serializable) object);
        startActivity(intent, (Activity) context);
    }

    /**
     * int 类型 + Object类型
     *
     * @param context
     * @param cls
     */
    public void commonJump(Context context, Class<?> cls, Object object, int intTag) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_INT, intTag);
        intent.putExtra(TYPE_OBJECT, (Serializable) object);
        startActivity(intent, (Activity) context);
    }


    /**
     * int 类型 + Object类型
     *
     * @param context
     * @param cls
     */
    public void commonJump(Context context, Class<?> cls, Object object, long intTag) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_LONG, intTag);
        intent.putExtra(TYPE_OBJECT, (Serializable) object);
        startActivity(intent, (Activity) context);
    }

    /**
     * int 类型 + Object类型
     *
     * @param context
     * @param cls
     */
    public void commonJump(Context context, Class<?> cls, Object object, long longTag, int intTag) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_LONG, longTag);
        intent.putExtra(TYPE_INT, intTag);
        intent.putExtra(TYPE_OBJECT, (Serializable) object);
        startActivity(intent, (Activity) context);
    }


    /**
     * int 类型 + Object类型
     *
     * @param context
     * @param cls
     */
    public void commonJump(Context context, Class<?> cls, String object, long longTag, int intTag) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_LONG, longTag);
        intent.putExtra(TYPE_INT, intTag);
        intent.putExtra(TYPE_STRING, object);
        startActivity(intent, (Activity) context);
    }

    public void commonJump(Context context, Class<?> cls, boolean b) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_BOOLEAN, b);
        startActivity(intent, (Activity) context);
    }

    public void commonJump(Context context, Class<?> cls, boolean b, boolean b2) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_BOOLEAN, b);
        intent.putExtra(TYPE_BOOLEAN2, b2);
        startActivity(intent, (Activity) context);
    }

    public void commonJump(Context context, Class<?> cls, boolean b, boolean b2, boolean b3) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_BOOLEAN, b);
        intent.putExtra(TYPE_BOOLEAN2, b2);
        intent.putExtra(TYPE_BOOLEAN3, b3);
        startActivity(intent, (Activity) context);
    }

    /**
     * int 类型 + Object类型
     *
     * @param context
     * @param cls
     */
    public void commonJump(Context context, Class<?> cls, Object object, boolean booleanTYpe, int intTag) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_BOOLEAN, booleanTYpe);
        intent.putExtra(TYPE_INT, intTag);
        intent.putExtra(TYPE_OBJECT, (Serializable) object);
        startActivity(intent, (Activity) context);
    }

    /**
     * int 类型 + Object类型
     *
     * @param context
     * @param cls
     */
    public void commonJump(Context context, Class<?> cls, Object object, String stringTag, int intTag) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_INT, intTag);
        intent.putExtra(TYPE_STRING, stringTag);
        intent.putExtra(TYPE_OBJECT, (Serializable) object);
        startActivity(intent, (Activity) context);
    }

    public void jump(Context context, Class cls, String key, Serializable value) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(key, value);
        context.startActivity(intent);
    }

    public void jump(Context context, Class cls, String key0, Serializable value0, String key1, Serializable value1) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(key0, value0);
        intent.putExtra(key1, value1);
        context.startActivity(intent);
    }

    /**
     * 简单的获取int参数的方法
     *
     * @param context
     */
    public static int getInteger(Activity context) {
        return context.getIntent().getIntExtra(TYPE_INT, 0);
    }

    public void commonJump(Context context, Class<?> cls, Object object) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_OBJECT, (Serializable) object);
        context.startActivity(intent);
    }

    public void commonJumpService(Context context, Class<?> cls, int index1, int index2, Object object) {
        Intent intent = new Intent(context, cls);
        intent.putExtra(TYPE_INT, index1);
        intent.putExtra(TYPE_INT2, index2);
        intent.putExtra(TYPE_OBJECT, (Serializable) object);
        context.startService(intent);
    }

    /**
     * 简单的获取Object参数的方法
     *
     * @param context
     */
    public static Object getObject(Activity context) {
        return context.getIntent().getSerializableExtra(TYPE_OBJECT);
    }

    public static Object getObject2(Activity context) {
        return context.getIntent().getSerializableExtra(TYPE_OBJECT2);
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
     * 简单的获取String参数的方法
     *
     * @param context
     */
    public static String getString(Activity context, String defaultValue) {
        String content = context.getIntent().getStringExtra(TYPE_STRING);
        if (content == null) {
            return defaultValue;
        }
        return content;
    }

    /**
     * 简单的获取Boolean参数的方法
     *
     * @param context
     */
    public static boolean getBoolean(Activity context) {
        return context.getIntent().getBooleanExtra(TYPE_BOOLEAN, false);
    }

    public static boolean getBoolean2(Activity context) {
        return context.getIntent().getBooleanExtra(TYPE_BOOLEAN2, false);
    }

    public static boolean getBoolean3(Activity context) {
        return context.getIntent().getBooleanExtra(TYPE_BOOLEAN3, false);
    }

    /**
     * 简单的获取String参数的方法
     *
     * @param context
     */
    public static String getString2(Activity context) {
        return context.getIntent().getStringExtra(TYPE_STRING2);
    }

}
