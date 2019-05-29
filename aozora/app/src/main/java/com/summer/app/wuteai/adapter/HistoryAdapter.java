package com.summer.app.wuteai.adapter;

import java.util.List;

import com.bumptech.glide.Glide;
import com.ferris.browser.MainActivity;
import com.summer.app.wuteai.entity.UrlInfo;
import com.summer.app.wuteai.utils.JumpTo;
import com.summer.asozora.livedoor.R;
import com.summer.asozora.livedoor.SortItemActivity;
import com.summer.db.CommonService;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HistoryAdapter  extends BaseViewAdapter{
	private Context context;
	private List<UrlInfo> sortList;
	private ViewHolder viewHodler;
	private CommonService mService;
//	wo1没有 nn1没有
	public HistoryAdapter(Context context){
		super(context);
		this.context = context;
		mService = new CommonService(context);
	}

	@Override
	public int getCount() {
		return sortList != null ?  sortList.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return sortList.get(position);
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
            final UrlInfo url = sortList.get(position);
            Glide.with(context).load(url.getStringLogo()).into(viewHodler.ivIcon);
			viewHodler.tvName.setText(url.getName());
			viewHodler.rlParentLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					JumpTo.getInstance().commonJump(context, MainActivity.class,url);					
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
