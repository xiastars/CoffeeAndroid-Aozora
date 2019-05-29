package com.summer.app.wuteai.adapter;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.bumptech.glide.Glide;
import com.ferris.browser.MainActivity;
import com.summer.app.wuteai.entity.UrlInfo;
import com.summer.app.wuteai.utils.JumpTo;
import com.summer.app.wuteai.utils.ReceiverUtils.ReceiverPre;
import com.summer.asozora.livedoor.R;
import com.summer.db.CommonService;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SortItemAdapter  extends BaseViewAdapter{
	private Context context;
	private List<UrlInfo> sortList;
	private ViewHolder viewHodler;
	private String MAIN_URL = "http://www.aozora.gr.jp/";
	private CommonService mService;
//	wo1没有 nn1没有
	public SortItemAdapter(Context context){
		super(context);
		this.context = context;
		mService = new CommonService(context);
	}

	@Override
	public int getCount() {
		return sortList != null ?  sortList.size(): 0;
	}

	@Override
	public Object getItem(int position) {
		return sortList.size();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = getInflaterView(R.layout.item_sort2);
			viewHodler = getViewHolder(convertView);
			convertView.setTag(viewHodler);
		} else {
			viewHodler = (ViewHolder) convertView.getTag();
		}
		if(sortList != null){
            final UrlInfo info = sortList.get(position);
			viewHodler.tvName.setText(info.getName());
			Glide.with(context).load(info.getStringLogo()).into(viewHodler.ivIcon);
			viewHodler.tvTime.setText(info.getTime());
			viewHodler.rlParentLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					getData(info);
				}
					
			});
		}
		return convertView;
	}
	
	private void getData(final UrlInfo info) {
		new Thread(new Runnable() {
   			
   			@Override
   			public void run() {
   		    	Document getCode;
   		        
   				try {
   					String getCityCode = "http://trans.hiragana.jp/ruby/"+info.getUrl();
   					getCode = Jsoup.connect(getCityCode).get();
   					Element image = getCode.select(".articleBody").first();
   					if(image != null){
   						Element span = image.select("span").first();
   						info.setContent(span.html());
   						mHandler.obtainMessage(0, info).sendToTarget();
   					}
   					

   				} catch (IOException e) {
   					e.printStackTrace();
   				}
   			}
   		}).start();;   
	}
	
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			UrlInfo url = (UrlInfo) msg.obj;
			mService.isShortcutExist(url);
			context.sendBroadcast(new Intent(ReceiverPre.NOTIFY_MAIN));
			JumpTo.getInstance().commonJump(context, MainActivity.class,url);	
		};
	};
	
	class ViewHolder {
		RelativeLayout rlParentLayout;
		TextView tvName;
		ImageView ivIcon;
		TextView tvTime;

	}
	
	public ViewHolder getViewHolder(View view){
		viewHodler = new ViewHolder();
		viewHodler.rlParentLayout = (RelativeLayout) view.findViewById(R.id.rl_memebers_list_layout);
		viewHodler.tvName = (TextView) view.findViewById(R.id.tvName);
		viewHodler.tvTime = (TextView) view.findViewById(R.id.tv_time);
		viewHodler.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
		return viewHodler;
	}

	@Override
	public void notifyDataChanged(List<?> arrsList) {
		this.sortList = (List<UrlInfo>) arrsList;
		notifyDataSetChanged();
	}
}
