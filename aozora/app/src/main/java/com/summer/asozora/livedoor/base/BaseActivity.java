package com.summer.asozora.livedoor.base;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.balanx.nfhelper.adapter.SRecycleMoreAdapter;
import com.balanx.nfhelper.view.SmartRecyclerView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.summer.asozora.livedoor.utils.ReceiverUtils;

/**
 * Created by xiaqiliang on 2017/3/24.
 */
public abstract class BaseActivity extends BaseRequestActivity {
    protected SmartRecyclerView sRecycleView;
    SmartRecyclerView scrollView;

    ReceiverUtils receiverUtils;

    public void setSRecyleViewForGrid(final SmartRecyclerView svContainer, int spanCount) {
        this.sRecycleView = svContainer;
        svContainer.setGridView(spanCount);
        svContainer.setEnableAutoLoadMore(true);
        svContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                fromId = 0;
                pageIndex = 0;
                isRefresh = true;
                loadData();
            }
        });
        svContainer.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pageIndex++;
                        isRefresh = true;
                        loadData();
                    }
                }, 2000);
            }
        });
    }


    public void showBackTop(int last, LinearLayout llBackTop, final RecyclerView svContainer ){
        if(last > BaseHelper.DEFAULT_LOAD_COUNT){
            llBackTop.setVisibility(View.VISIBLE);
            llBackTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    svContainer.scrollToPosition(0);
                }
            });
        }else{
            llBackTop.setVisibility(View.GONE);
        }
    }

    @Override
    public void initPresenter() {

    }


    public void refreshData() {
        pageIndex = 0;
        lastId = null;
        loadData();
    }


    protected void setsRecycleViewAdapter(SRecycleMoreAdapter adapter) {
        sRecycleView.setAdapter(adapter);
    }

    public void handleViewData(Object obj) {
        if (sRecycleView == null) {
            return;
        }
        if (sRecycleView.getRefreshViewForTypeRecycleView() == null) {
            return;
        }
        baseHelper.handleViewData(obj, sRecycleView, pageIndex);
    }

    public long getHandleTime() {
        return baseHelper.getHandleTime();
    }

    public void showLoadingDialogWithRequest(boolean show) {
        baseHelper.setShowLoading(show);
    }

    /**
     * 注册广播
     *
     * @param action
     */
    protected void initBroadcast(String... action) {
        if (receiverUtils != null) {
            receiverUtils.unRegisterReceiver();
        }
        receiverUtils = new ReceiverUtils(this);
        receiverUtils.setActionsAndRegister(action);
        receiverUtils.setOnReceiverListener(new ReceiverUtils.ReceiverListener() {
            @Override
            public void doSomething(String action, Intent intent) {
                if (context == null) {
                    return;
                }
                onMsgReceiver(action, intent);
            }
        });
    }

    protected void sendBroadcast(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);
    }


    protected void onMsgReceiver(String action, Intent intent) {
        if (context == null) {
            return;
        }
    }

    @Override
    protected void loadData() {
    }

    @Override
    protected void finishLoad() {

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        context = null;
        if (receiverUtils != null) {
            receiverUtils.unRegisterReceiver();
        }
    }
}