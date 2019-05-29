package com.summer.app.wuteai.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.summer.app.wuteai.entity.UrlInfo;
import com.summer.app.wuteai.utils.JumpTo;
import com.summer.asozora.livedoor.R;
import com.summer.asozora.livedoor.SortItemActivity;
import com.summer.db.CommonService;
import com.summer.db.CommonType;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SingleUrlAdapter  extends BaseViewAdapter{
	private Context context;
	private String[] sortList;
	private ViewHolder viewHodler;
	private CommonService mService;
	private String MAIN_URL = "http://www.aozora.gr.jp/index_pages/sakuhin_";
	private String[] SUFFIX = {"a1","ka1","sa1","ta1","na1","ha1","ma1","ya1","ra1","wa1","i1","ki1","si1","ti1","hi1","mi1","ri1","u1"
			,"ku1","su1","tu1","nu1","hu1","mu1","yu1","ru1","e1","ke1","se1","te1","ne1","he1","me1","re1","o1","ko1","so1","to1"
			,"no1","ho1","mo1","yo1","ro1","zz1"};
//	wo1没有 nn1没有
	public SingleUrlAdapter(Context context){
		super(context);
		this.context = context;
		mService = new CommonService(context);
	}

	@Override
	public int getCount() {
		return sortList != null ?  sortList.length+3: 0;
	}

	@Override
	public Object getItem(int position) {
		return sortList.length;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = getInflaterView(R.layout.item_sort);
			viewHodler = getViewHolder(convertView);
			convertView.setTag(viewHodler);
		} else {
			viewHodler = (ViewHolder) convertView.getTag();
		}
		if(sortList != null){

		    if(position == 47 || position == 46 || position == 45){
		    	viewHodler.tvName.setText("");
		    }else{
	            viewHodler.tvName.setText(sortList[position]);
		    }
			viewHodler.rlParentLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					List<UrlInfo> mMovies = (List<UrlInfo>) mService.getListData(CommonType.BOOK_LIST,SUFFIX[position]);
					if(mMovies != null && mMovies.size() > 0){
						mHandler.obtainMessage(0, mMovies).sendToTarget();
					}else{
						getData(position);
					}					
				}
			});
		}
		return convertView;
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			List<UrlInfo> urls = (List<UrlInfo>) msg.obj;
			JumpTo.getInstance().commonJump(context, SortItemActivity.class,urls);	
		};
	};
	
	class ViewHolder {
		RelativeLayout rlParentLayout;
		TextView tvName;

	}
	
	private void getData(final int position) {
		new Thread(new Runnable() {
   			
   			@Override
   			public void run() {
   		    	Document getCode;
   		        
   				try {
   					String getCityCode = MAIN_URL+SUFFIX[position]+".html";
   					Log.i("url-t", getCityCode+"---");
   					getCode = Jsoup.connect(getCityCode).get();
   					Elements image = getCode.select("a[href]");
   					List<UrlInfo> mMovies = new ArrayList<UrlInfo>();
   					if(image != null){
	   				
	   					for(Element el : image){
	   						String url = el.attr("href");
	   						String name = el.text();
	   						if(url != null && url.contains("cards")){
	   							UrlInfo info = new UrlInfo();
		   						info.setUrl(url);
		   						info.setName(name);
		   						mMovies.add(info);
	   						}
	   					
	   					}
   					}
   					if(mMovies != null && mMovies.size() > 0){
   						mService.insert(CommonType.BOOK_LIST,SUFFIX[position], mMovies);
   						mHandler.obtainMessage(0, mMovies).sendToTarget();
   					}
   				} catch (IOException e) {
   					e.printStackTrace();
   				}
   			}
   		}).start();;   
	}
	
	public ViewHolder getViewHolder(View view){
		viewHodler = new ViewHolder();
		viewHodler.rlParentLayout = (RelativeLayout) view.findViewById(R.id.rl_memebers_list_layout);
		viewHodler.tvName = (TextView) view.findViewById(R.id.tvName);
		return viewHodler;
	}

	public void notifyDataChanged(String[] arrsList) {
		this.sortList = arrsList;
		notifyDataSetChanged();
	}

	@Override
	public void notifyDataChanged(List<?> arrsList) {
		
	}
}
