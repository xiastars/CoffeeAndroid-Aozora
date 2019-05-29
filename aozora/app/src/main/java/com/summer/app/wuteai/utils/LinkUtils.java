package com.summer.app.wuteai.utils;

import android.graphics.Color;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.widget.TextView;

public class LinkUtils {
	
	public static final String SCHEMA = "ilg://";
	
	/** 登录 */
	public static final String SCHEMA_LOGIN = SCHEMA + "app_login";
	/** 注册 */
	public static final String SCHEMA_REGISTER = SCHEMA + "app_register";
	/** 支付 */
	public static final String SCHEMA_PAY = SCHEMA + "app_pay";
	/** 附近 */
	public static final String SCHEMA_NEARBY = SCHEMA + "app_nearby";
	/** 用户 */
	public static final String SCHEMA_USER = SCHEMA + "app_user";
	/** 用户语聊 */
	public static final String SCHEMA_CHAT = SCHEMA + "app_chat";
	/** 用户分享 */
	public static final String SCHEMA_SHARE = SCHEMA + "app_share";
	/** 钱包 */
	public static final String SCHEMA_WALLET = SCHEMA + "app_wallet";
	
	
	/** 充值金额 */
	public static final String PARAM_PRICE = "price";
	/** 路径 */
	public static final String PARAM_URL = "url";
	/** ID */
	public static final String PARAM_ID = "id";
	/** PID */
	public static final String PARAM_PID = "pid";
	/** COUNT */
	public static final String PARAM_COUNT = "count";
	/** CODE */
	public static final String PARAM_CODE = "code";
	
	
	public static void stripUnderlines(TextView textView) {
		if (textView == null)
			return;
		try {
			final Spannable s = (Spannable) textView.getText();
			URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
			for (URLSpan span : spans) {
				final int start = s.getSpanStart(span);
				final int end = s.getSpanEnd(span);
				s.removeSpan(span);
				span = new URLSpanNoUnderline(span.getURL());
				s.setSpan(span, start, end, 0);
			}
			textView.setText(s);
		} catch (ClassCastException e) {
			//
		}
	}
	
	private static class URLSpanNoUnderline extends URLSpan {

		public URLSpanNoUnderline(String url) {
			super(url);
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setColor(Color.parseColor("#33b5e5"));
			ds.setUnderlineText(false);
		}
	}
}
