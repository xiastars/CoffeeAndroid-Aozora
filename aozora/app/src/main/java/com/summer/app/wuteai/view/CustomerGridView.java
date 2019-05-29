package com.summer.app.wuteai.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class CustomerGridView extends GridView{

	public CustomerGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomerGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomerGridView(Context context) {
		super(context);
	}

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// MeasureSpec由大小和模式组成。它有三种模式：UNSPECIFIED(未指定),父元素部队自元素施加任何束缚，
		// 子元素可以得到任意想要的大小；EXACTLY(完全)，父元素决定自元素的确切大小，
		// 子元素将被限定在给定的边界里而忽略它本身大小；AT_MOST(至多)，子元素至多达到指定大小的值。
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
