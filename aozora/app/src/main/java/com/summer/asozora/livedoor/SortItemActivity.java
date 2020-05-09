package com.summer.asozora.livedoor;

import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.balanx.nfhelper.utils.SUtils;
import com.balanx.nfhelper.view.NRecycleView;
import com.summer.app.wuteai.adapter.SortItemAdapter;
import com.summer.app.wuteai.entity.UrlInfo;
import com.summer.app.wuteai.utils.JumpTo;

import java.util.List;

public class SortItemActivity extends Activity {
	NRecycleView mGridView;
	
	private SortItemAdapter mAdapter;
	private Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_sort_item);

		initView();
	}

	private void initView() {
		mGridView = (NRecycleView) findViewById(R.id.gv_sort);
		mGridView.setGridView(3);
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
