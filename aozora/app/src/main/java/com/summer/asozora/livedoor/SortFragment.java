package com.summer.asozora.livedoor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.summer.app.wuteai.adapter.SortItemAdapter;
import com.summer.app.wuteai.entity.UrlInfo;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SortFragment extends Fragment {
	PullToRefreshListView mGridView;
	
	private SortItemAdapter mAdapter;
	private Context context;
	private String NEWS_MAIN = "http://news.livedoor.com/straight_news/";
	
	public SortFragment(){
		
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
		mGridView.setMode(Mode.PULL_FROM_START);
		mGridView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				getData();
			}
		});
		mAdapter = new SortItemAdapter(context);
		
//		mAdapter.notifyDataChanged(mUrls);
		getData();
		
	}
	
	private void getData() {
		new Thread(new Runnable() {
   			
   			@Override
   			public void run() {
   		    	Document getCode;
   		        
   				try {
   					String getCityCode = NEWS_MAIN;
   					Log.i("url-t", getCityCode+"---");
   					getCode = Jsoup.connect(getCityCode).get();
   					List<UrlInfo> urls = new ArrayList<UrlInfo>();
   					Elements image = getCode.select(".hasImg");
   					if(image != null){	   				
	   					for(Element el : image){
	   						String url = null;
	   						Element urlEl = el.select("a[href]").first();
	   						if(urlEl != null){
	   							url = urlEl.attr("href");
	   							if(url.contains("topics")){
	   								url = url.replace("topics", "article");
	   							}
	   						}
	   						String time = null;
	   						Element timeEl = el.select("time").first();
	   						if(timeEl != null){
	   							time = timeEl.text();
	   						}
	   						String title = null;
	   						String img = null;
	   						Element strightContent = el.select(".straightContent").first();
	   						if(strightContent != null){
	   							
	   							Element strightImg = strightContent.select(".straightImg").first();
	   							if(strightImg != null){
	   								img = strightImg.select("img[src]").first().attr("src");
	   							}
	   							Element straightBody = strightContent.select(".straightBody").first();
	   							Log.i("straightBody", straightBody+"---");
	   							if(straightBody != null){
	   								title = straightBody.select(".straightTtl").first().text();
	   							}
	   						}
	   						if(!TextUtils.isEmpty(title)){
	   							UrlInfo info = new UrlInfo();
		   						info.setName(title);
		   						info.setStringLogo(img);
		   						info.setUrl(url);
		   						info.setTime(time);
		   						urls.add(info);
	   						}
	   						
	   						
	   						Log.i("rul...", url+"---"+title+"--"+time);
	   					}
   					}
   					mHandler.obtainMessage(0, urls).sendToTarget();

   				} catch (IOException e) {
   					e.printStackTrace();
   				}
   			}
   		}).start();;   
	}
	
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case 0:
				mGridView.onRefreshComplete();
				List<UrlInfo> urls = (List<UrlInfo>) msg.obj;
				mGridView.setAdapter(mAdapter);
				mAdapter.notifyDataChanged(urls);
				break;
			}
		};
	};
	

}
