package com.summer.other;

import java.util.List;

import com.summer.asozora.livedoor.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class SummerAc extends Fragment{
	private List<SummerApp> appList;
	AppAdapter adapter;
	ListView listView;
	private Context mContext;
	
    public SummerAc(){
		
	}	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this.getActivity();
    	Bmob.initialize(mContext, "826a4061eda2fa84bcbb33f9b17b6011");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.ac_summerapp, null);
	    listView = (ListView) rootView.findViewById(R.id.mTrackListView);
		adapter = new AppAdapter(mContext);
	    listView.setAdapter(adapter);
	    getData();
		return rootView;
	}

    private void getData(){
    	BmobQuery<SummerApp> query = new BmobQuery<SummerApp>();
    	query.setLimit(1000);
    	query.order("-updatedAt");
    	query.findObjects(mContext, new FindListener<SummerApp>() {
			
			@Override
			public void onSuccess(List<SummerApp> s) {
				appList = s;
				Log.i("s...",s.size()+"------");
				handler.sendEmptyMessage(0);
			}
			
			@Override
			public void onError(int arg0, String arg1) {
			}
		});
    }
    
    private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				
				if(appList !=null && appList.size()>0){
					adapter.notifyData(appList);
				}
				
				break;
			}
		};
	};


}
