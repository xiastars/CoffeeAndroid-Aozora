package com.summer.asozora.livedoor;

import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.summer.app.wuteai.adapter.HistoryAdapter;
import com.summer.app.wuteai.entity.UrlInfo;
import com.summer.db.CommonService;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HistoryFragment extends Fragment {
	PullToRefreshListView mGridView;
	
	private HistoryAdapter mAdapter;
	private Context context;
	
	public HistoryFragment(){
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this.getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.ac_sort_item, null);
		initView(rootView);
		return rootView;
	}

	private void initView(View rootView) {
		mGridView = (PullToRefreshListView) rootView.findViewById(R.id.gv_sort);
		mAdapter = new HistoryAdapter(context);
		mGridView.setAdapter(mAdapter);
		List<UrlInfo> urls = new CommonService(context).getRecentShortcuts(100, 1);
		mAdapter.notifyDataChanged(urls);
		
		
	}
	

}
