package com.summer.asozora.livedoor;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.balanx.nfhelper.view.SmartRecyclerView;
import com.summer.app.wuteai.adapter.HistoryAdapter;

public class HistoryFragment extends Fragment {
	SmartRecyclerView mGridView;
	
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
		mGridView = (SmartRecyclerView) rootView.findViewById(R.id.gv_sort);
		mAdapter = new HistoryAdapter(context);
		mGridView.setAdapter(mAdapter);
		//List<UrlInfo> urls = new CommonService(context).getRecentShortcuts(100, 1);
		//mAdapter.notifyDataChanged(urls);
		
		
	}
	

}
