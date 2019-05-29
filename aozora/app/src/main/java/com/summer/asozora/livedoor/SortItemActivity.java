package com.summer.asozora.livedoor;

import java.util.List;

import com.summer.app.wuteai.adapter.SortItemAdapter;
import com.summer.app.wuteai.entity.UrlInfo;
import com.summer.app.wuteai.utils.JumpTo;
import com.summer.app.wuteai.utils.SUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class SortItemActivity extends Activity {
	ListView mGridView;
	
	private SortItemAdapter mAdapter;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_sort_item);

		initView();
	}

	private void initView() {
		mGridView = (ListView) findViewById(R.id.gv_sort);
		RelativeLayout.LayoutParams clp = ( RelativeLayout.LayoutParams) mGridView.getLayoutParams();
		if(VERSION.SDK_INT >= 19){			  
		    clp.topMargin = SUtils.getStatusBarHeight(this);
		}else{
	
		}
		mAdapter = new SortItemAdapter(this);
		mGridView.setAdapter(mAdapter);
		List<UrlInfo> urls = (List<UrlInfo>) JumpTo.getInstance().getObject(this);
		mAdapter.notifyDataChanged(urls);
		
		
	}
	

}
