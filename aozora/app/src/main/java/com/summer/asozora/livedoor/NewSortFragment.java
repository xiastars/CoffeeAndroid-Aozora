package com.summer.asozora.livedoor;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.balanx.nfhelper.utils.Logs;
import com.balanx.nfhelper.view.SmartRecyclerView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.summer.app.wuteai.adapter.SortItemAdapter;
import com.summer.app.wuteai.entity.UrlInfo;
import com.summer.asozora.livedoor.base.BaseFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @Description:
 * @Author: xiastars@vip.qq.com
 * @CreateDate: 2020/5/9 15:22
 */
public class NewSortFragment extends BaseFragment {
    @BindView(R.id.nv_container)
    SmartRecyclerView nvContainer;

    SortItemAdapter commonAdapter;

    private String NEWS_MAIN = "http://news.livedoor.com/straight_news/";


    @Override
    protected void initView(View view) {
        //设置为List样式
        nvContainer.setList();
        commonAdapter = new SortItemAdapter(context);
        nvContainer.setAdapter(commonAdapter);
        //开启自动加载功能（非必须）
        nvContainer.setEnableAutoLoadMore(true);
        nvContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                Logs.i("---------");
                getData();
            }
        });
        nvContainer.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (commonAdapter.getItemCount() > 30) {
                            Toast.makeText(context, "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                            refreshLayout.finishLoadMoreWithNoMoreData();//将不会再次触发加载更多事件
                        } else {
                            List<String> datas = new ArrayList<>();
                            for (int i = 0; i < 30; i++) {
                                datas.add("萍水相逢萍水散，各自天涯各自安。");
                            }
                            commonAdapter.notifyDataChanged(datas);
                            refreshLayout.finishLoadMore();
                        }
                    }
                }, 2000);
            }
        });

        //触发自动刷新
        nvContainer.autoRefresh();
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
                    myHandlder.obtainMessage(0, urls).sendToTarget();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();;
    }

    @Override
    protected void handleMsg(int position, Object object) {
        super.handleMsg(position, object);
        switch (position) {
            case 0:
                nvContainer.finishRefresh();
                List<UrlInfo> urls = (List<UrlInfo>) object;
                commonAdapter.notifyDataChanged(urls);
                break;
        }
    }

    @Override
    public void loadData() {

    }

    @Override
    protected void dealDatas(int requestType, Object obj) {

    }

    @Override
    protected int setContentView() {
        return R.layout.ac_sort_item;
    }
}
