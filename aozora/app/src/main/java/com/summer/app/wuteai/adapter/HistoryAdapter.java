package com.summer.app.wuteai.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balanx.nfhelper.adapter.SRecycleMoreAdapter;
import com.balanx.nfhelper.db.CommonService;
import com.balanx.nfhelper.utils.SUtils;
import com.summer.app.wuteai.entity.UrlInfo;
import com.summer.app.wuteai.utils.JumpTo;
import com.summer.asozora.livedoor.MainActivity;
import com.summer.asozora.livedoor.R;
import com.summer.asozora.livedoor.SortItemActivity;

import java.util.List;

public class HistoryAdapter  extends SRecycleMoreAdapter {
	private Context context;
	private List<UrlInfo> sortList;
	private ViewHolder viewHodler;
	private CommonService mService;
//	wo1没有 nn1没有
	public HistoryAdapter(Context context){
		super(context);
		mService = new CommonService(context);
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


	@Override
	public RecyclerView.ViewHolder setContentView(ViewGroup parent) {
		return new SortItemAdapter.ViewHolder(createHolderView(R.layout.item_sort2, null));
	}

	@Override
	public void bindContentView(RecyclerView.ViewHolder holder, int position) {
			final UrlInfo url = sortList.get(position);
			SUtils.setPic(viewHodler.ivIcon,url.getStringLogo());
			viewHodler.tvName.setText(url.getName());
			viewHodler.rlParentLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					JumpTo.getInstance().commonJump(context, MainActivity.class,url);
				}
			});

	}
}
