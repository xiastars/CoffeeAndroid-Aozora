package com.summer.asozora.livedoor;

import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.balanx.nfhelper.utils.SUtils;
import com.summer.app.wuteai.view.CustomerViewPager;
import com.summer.app.wuteai.view.PagerSlidingTabStrip;
import com.summer.asozora.livedoor.base.BaseFragmentActivity;
import com.summer.asozora.livedoor.constd.ReceiverPre;
import com.summer.asozora.livedoor.utils.ReceiverUtils;

import java.util.List;

public class AsozoraActivity extends BaseFragmentActivity implements OnClickListener {
    PagerSlidingTabStrip tabStrip;
    CustomerViewPager mViewPager;
    RelativeLayout rlParent;
    private PagerAdapter mPagerAdapter;
    private Context context;
    private ReceiverUtils mReceiver;

    private void initView() {
        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mViewPager = (CustomerViewPager) findViewById(R.id.content_pager);
        rlParent = (RelativeLayout) findViewById(R.id.rl_tab);
        RelativeLayout.LayoutParams clp = (RelativeLayout.LayoutParams) rlParent.getLayoutParams();
        if (VERSION.SDK_INT >= 19) {
            clp.height = SUtils.getDip(context, 52) + SUtils.getStatusBarHeight((Activity) context);
        } else {
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
        initBroadcast(ReceiverPre.NOTIFY_MAIN);

        loadUrl();
    }

    private void loadUrl() {

    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            List<Shuadianji> s = (List<Shuadianji>) msg.obj;

            for (Shuadianji dianji : s) {
                WebView web = new WebView(AsozoraActivity.this);
                web.loadUrl(dianji.getUrl());
            }
        }

        ;
    };

    public class PagerAdapter extends FragmentStatePagerAdapter {
        String[] sorts = {"ニュース", "歴史", "私の"};

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    return new NewSortFragment();
                case 1:
                    return new HistoryFragment();
                default:
                    return new NewSortFragment();
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
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //得到缓存的fragment
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            return fragment;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            mReceiver.unRegisterReceiver();
        }
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void finishLoad() {

    }

    @Override
    protected void dealDatas(int requestCode, Object obj) {

    }

    @Override
    protected int setTitleId() {
        return 0;
    }

    @Override
    protected int setContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        initView();
    }

    @Override
    public void onClick(View v) {
    }

    public void notifiDatachanged() {
        mPagerAdapter.notifyDataSetChanged();
    }

}
