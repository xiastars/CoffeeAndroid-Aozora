package com.summer.asozora.livedoor;

import java.util.List;

import com.summer.app.wuteai.utils.ReceiverUtils;
import com.summer.app.wuteai.utils.ReceiverUtils.ReceiverListener;
import com.summer.app.wuteai.utils.ReceiverUtils.ReceiverPre;
import com.summer.app.wuteai.utils.SUtils;
import com.summer.app.wuteai.view.CustomerViewPager;
import com.summer.app.wuteai.view.PagerSlidingTabStrip;
import com.summer.other.SummerAc;
import com.summer.other.SummerApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class AsozoraActivity extends FragmentActivity implements OnClickListener{
	PagerSlidingTabStrip tabStrip;
	CustomerViewPager mViewPager;
	RelativeLayout rlParent;
	private PagerAdapter mPagerAdapter;
	private Context context;
	private ReceiverUtils mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		initView();
	}

	private void initView() {
		tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mViewPager = (CustomerViewPager) findViewById(R.id.content_pager);
		rlParent = (RelativeLayout) findViewById(R.id.rl_tab);
		RelativeLayout.LayoutParams clp = ( RelativeLayout.LayoutParams) rlParent.getLayoutParams();
		if(VERSION.SDK_INT >= 19){			  
		    clp.height = SUtils.getDip(context, 52)+SUtils.getStatusBarHeight((Activity) context);
		}else{
			clp.height = SUtils.getDip(context, 52);
		}
		tabStrip.setTextColor(context.getResources().getColor(R.color.white));
		tabStrip.setAssitTextColor(context.getResources().getColor(R.color.tran_white));
		tabStrip.setTextSize(SUtils.getDip(context, 20));
		tabStrip.setUnderlineHeight(SUtils.getDip(context, 1));
		tabStrip.setDividerPadding(SUtils.getDip(context, 12));
		mViewPager.setOffscreenPageLimit(2);
		mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mPagerAdapter);
		tabStrip.setViewPager(mViewPager);
		mReceiver = new ReceiverUtils(this);
		mReceiver.setActionsAndRegister(ReceiverPre.NOTIFY_MAIN);
		mReceiver.setOnReceiverListener(new ReceiverListener() {
			
			@Override
			public void doSomething(int position, Intent intent) {
				switch(position){
				case 0:
					mPagerAdapter.notifyDataSetChanged();
					break;
				}
			}
		});
		
		loadUrl();
	}
	
	private void loadUrl() {
		Bmob.initialize(this, "826a4061eda2fa84bcbb33f9b17b6011");
		BmobQuery<Shuadianji> query = new BmobQuery<Shuadianji>();
    	query.setLimit(1000);
    	query.addWhereEqualTo("show", 1);
    	query.order("-updatedAt");
    	query.findObjects(this, new FindListener<Shuadianji>() {
			
			@Override
			public void onSuccess(List<Shuadianji> s) {
				if(s != null && s.size() > 0){
					mHandler.obtainMessage(0, s).sendToTarget();
				}
			}
			
			@Override
			public void onError(int arg0, String arg1) {
			}
		});
	}
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			List<Shuadianji> s = (List<Shuadianji>) msg.obj;
			
			for(Shuadianji dianji : s){
				WebView web =new WebView(AsozoraActivity.this);
				web.loadUrl(dianji.getUrl());
			}
		};
	};

	public class PagerAdapter extends FragmentStatePagerAdapter {
		 String[] sorts = {"ニュース","歴史","私の"};
	    public PagerAdapter(FragmentManager fm) {
	    	super(fm);
	    }

	    @Override
	    public Fragment getItem(int pos) {
		      switch(pos){
		      case 0:	    	  
		    	  return new SortFragment();
		      case 1:
		    	  return new HistoryFragment();
		      case 2:
		    	  return new SummerAc();
		      default:
		    	  return new SortFragment();
		      }
	    }

	    @Override
	    public int getCount() {
	      return sorts.length;
	    }

	    @Override
	    public CharSequence getPageTitle(int position) {
	      return sorts[position];
	    }
	    
	    @Override  
	    public int getItemPosition(Object object) 
	    {  
	        return POSITION_NONE;  
	    }  
	    
	    @Override
	    public Object instantiateItem(ViewGroup container,int position) {
	       //得到缓存的fragment
	       Fragment fragment = (Fragment)super.instantiateItem(container,position);
	       return fragment;
	    }
		  

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mReceiver != null){
			mReceiver.unRegisterReceiver();
		}
	}

	@Override
	public void onClick(View v) {
	}

	public void notifiDatachanged() {
		mPagerAdapter.notifyDataSetChanged();
	}

}
