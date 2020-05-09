package com.summer.app.wuteai.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balanx.nfhelper.adapter.SRecycleMoreAdapter;
import com.balanx.nfhelper.db.CommonService;
import com.balanx.nfhelper.utils.SUtils;
import com.summer.app.wuteai.entity.UrlInfo;
import com.summer.asozora.livedoor.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SortItemAdapter extends SRecycleMoreAdapter {

    private Context context;
    private List<UrlInfo> sortList;
    private ViewHolder viewHodler;
    private String MAIN_URL = "http://www.aozora.gr.jp/";
    private CommonService mService;

    //	wo1没有 nn1没有
    public SortItemAdapter(Context context) {
        super(context);
        this.context = context;
        mService = new CommonService(context);
    }

    @Override
    public RecyclerView.ViewHolder setContentView(ViewGroup parent) {
        return new ViewHolder(createHolderView(R.layout.item_sort2, null));
    }

    private void getData(final UrlInfo info) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Document getCode;

                try {
                    String getCityCode = "http://trans.hiragana.jp/ruby/" + info.getUrl();
                    getCode = Jsoup.connect(getCityCode).get();
                    Element image = getCode.select(".articleBody").first();
                    if (image != null) {
                        Element span = image.select("span").first();
                        info.setContent(span.html());
                        //mHandler.obtainMessage(0, info).sendToTarget();
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        ;
    }


    @Override
    public void bindContentView(RecyclerView.ViewHolder holder, int position) {
        if (sortList != null) {
            final UrlInfo info = sortList.get(position);
            viewHodler.tvName.setText(info.getName());
            SUtils.setPic(viewHodler.ivIcon,info.getStringLogo());
            viewHodler.tvTime.setText(info.getTime());
            viewHodler.rlMemebersListLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    getData(info);
                }

            });
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_icon)
        ImageView ivIcon;
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.headerLayout)
        RelativeLayout headerLayout;
        @BindView(R.id.line)
        View line;
        @BindView(R.id.rl_memebers_list_layout)
        RelativeLayout rlMemebersListLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
