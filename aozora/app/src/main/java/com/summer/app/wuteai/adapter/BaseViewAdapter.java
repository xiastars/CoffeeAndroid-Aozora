package com.summer.app.wuteai.adapter;

import java.util.List;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

/**
 * BaseViewAdapter
 * 
 */
public abstract class BaseViewAdapter extends BaseAdapter {

	public static final int BASE_HOME_TYPE_ADAPTER = 0;
	public static final int BASE_GROUP_TYPE_ADAPTER = 1;
	private LayoutInflater mInflater;
	private int type = BASE_HOME_TYPE_ADAPTER;

	public BaseViewAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
	}
	
	public BaseViewAdapter(){}

	/**
	 * 返回InflaterView
	 * 
	 * @param layoutId
	 * @return
	 */
	public View getInflaterView(int layoutId) {
		return mInflater.inflate(layoutId, null);
	}

	public abstract void notifyDataChanged(List<?> arrsList);

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends View> T get(View view, int id) {
		
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
}
