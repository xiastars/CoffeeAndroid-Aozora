package com.summer.asozora.livedoor.base;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.balanx.nfhelper.adapter.SRecycleMoreAdapter;
import com.balanx.nfhelper.db.CommonService;
import com.balanx.nfhelper.db.DBType;
import com.balanx.nfhelper.server.EasyHttp;
import com.balanx.nfhelper.server.PostData;
import com.balanx.nfhelper.server.RequestCallback;
import com.balanx.nfhelper.server.ServerFileUtils;
import com.balanx.nfhelper.server.SummerParameter;
import com.balanx.nfhelper.utils.Logs;
import com.balanx.nfhelper.utils.SThread;
import com.balanx.nfhelper.utils.SUtils;
import com.balanx.nfhelper.view.SmartRecyclerView;
import com.summer.asozora.livedoor.R;
import com.summer.asozora.livedoor.UIApplication;
import com.summer.asozora.livedoor.view.LoadingDialog;
import com.summer.resp.BaseResp;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaqiliang on 2017/5/2.
 */

public class BaseHelper {
    Context context;
    boolean firstRequest;
    Handler myHandlder;
    LoadingDialog loadingDialog;
    boolean isRefresh;
    long handleTime;
    public static final int DEFAULT_LOAD_COUNT = 10;//默认请求数量

    //请求数据失败
    public static final int MSG_ERRO = -1;
    //请求数据成功
    public static final int MSG_SUCCEED = -2;

    public static final int MSG_FINISHLOAD = -3;

    //从数据库读取目录成功
    public static final int MSG_CACHE = -4;


    //显示加载
    public static final int MSG_LOADING = -5;

    //取消加载
    public static final int MSG_CANCEL_LOADING = -6;

    //虚拟数据地址
    public static String VITURAL_DATA = "virtualdata.txt";//virtualdata.txt

    //当一个页面反复刷新时，只第一次插入
    boolean isFirstInsertDB;

    //特殊情况手动显示加载框
    boolean showLoading;

    //分页数量
    int loadCount = 0;

    public BaseHelper(Context context, Handler myHandlder) {
        this.context = context;
        this.myHandlder = myHandlder;
    }

    /**
     * 请求数据
     *
     * @param requestCode
     * @param className
     * @param params
     * @param url
     * @param post
     */
    public void requestData(final int requestCode, final Class className, SummerParameter params, final String url, boolean post) {
        requestData(requestCode, 0, className, params, url, post);
    }

    void setMIUIStatusBarDarkMode(Activity activity) {
        Logs.i("PostData.MANUFACTURER" + PostData.MANUFACTURER);
        if (PostData.MANUFACTURER.equals("Xiaomi")) {
            Class<? extends Window> clazz = activity.getWindow().getClass();
            try {
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                int darkModeFlag = field.getInt(layoutParams);

                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                extraFlagField.invoke(activity.getWindow(), darkModeFlag);
            } catch (Exception e) {
                e.printStackTrace();
            }
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    /**
     * 请求数据
     *
     * @param limitTime 数据重新请求限定时间
     * @param className 要注入的类
     * @param params
     * @param url       链接
     * @param post      是否是Post
     */
    public void requestData(final int requestCode, int limitTime, final Class className, SummerParameter params, final String url, boolean post) {
        requestData(requestCode, limitTime, className, params, url, post, false);
    }

    public void requestData(final int requestCode, int limitTime, final Class className, SummerParameter params, String url, boolean post, final boolean isArray) {
        requestData(requestCode, null, limitTime, className, params, url, post ? 1 : 0, isArray);
    }

    public void putData(final int requestCode, final Class className, SummerParameter params, String url) {
        requestData(requestCode, null, 0, className, params, url, 2, false);
    }

    public void putData(final int requestCode,String version, final Class className, SummerParameter params, String url) {
        requestData(requestCode, version, 0, className, params, url, 2, false);
    }

    public void deleteData(final int requestCode, final Class className, SummerParameter params, String url) {
        requestData(requestCode, null, 0, className, params, url, 3, false);
    }

    public void deleteData(final int requestCode,String version ,final Class className, SummerParameter params, String url) {
        requestData(requestCode, version, 0, className, params, url, 3, false);
    }


    /**
     * 请求数据
     *
     * @param limitTime 数据重新请求限定时间
     * @param className 要注入的类
     * @param params
     * @param url       链接
     * @param post      是否是Post
     * @param isArray   是不是Array类型
     */
    public void requestData(final int requestCode, String version, int limitTime, final Class className, SummerParameter params, String url, int post, final boolean isArray) {
        if (params == null) {
            return;
        }
        String token = "";
        //String token = SUtils.getStringData(context, SharePreConst.TOKEN);
        //Logs.i("token:" + token);
        //url = ApiConstants.getHost(version) + url;
        long time = System.currentTimeMillis();
        //取得每页请求的数量，用来处理底部栏没有更多了
        Logs.i("contain:" + params.containsKey("count"));
        if (params.containsKey("count")) {
            String count = (String) params.get("count");
            if (count != null) {
                loadCount = Integer.parseInt(count);
                Logs.i("loadCount:" + loadCount);
            }
        }
        if (params.containsKey("limit")) {
            String count = (String) params.get("limit");
            if (count != null) {
                loadCount = Integer.parseInt(count);
                Logs.i("loadCount:" + loadCount);
            }
        }
        final boolean toastMessage = params.isToastEnable();
        final int pageIndex = Integer.parseInt((String) params.getParamSecurity("page", 0 + ""));
        final boolean readCache = params.isCacheSupport();
        final String saveurl = params.encodeLogoUrl(url);
        //当为开发者模式且SummperParameter设置了虚拟数据时才启用
        boolean hasVirturData = false;
        if (UIApplication.DEBUGMODE) {
            if (params.isVirtualData()) {
                String virtualCode = params.getVirtualCode();
                //virtualdata.txt
                String data = ServerFileUtils.readFileByLineOnAsset(virtualCode, VITURAL_DATA, context);
                try {
                    data = data.replaceAll(">", "2");
                    data = data.replaceAll("<", "2");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Logs.i("data:" + data);
                if (data != null) {
                    try {
                        BaseResp t = JSON.parseObject(data, BaseResp.class);
                        Logs.i("t:::" + t);
                        if (t != null) {
                            myHandlder.sendEmptyMessage(MSG_FINISHLOAD);
                            handleData(requestCode, t, saveurl, className, pageIndex, readCache, isArray, toastMessage);
                            isRefresh = false;
                            hasVirturData = true;
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (hasVirturData) {
            return;
        }
        Logs.i("first:"+firstRequest);
        //页面启动第一次时从缓存里获取数据
        if (!firstRequest && readCache) {
            firstRequest = true;
            readCache(saveurl, requestCode, className, isArray);
        }
        Logs.i("readCache:" + readCache);
        //频率操作限制
        if (isFrequentRequest(saveurl, limitTime == 0 ? 1000 : limitTime)) {
            Logs.i("isFrequentRequest频繁操作");
            if (readCache(saveurl, requestCode, className, isArray)) {
                Logs.i("readCache");
                return;
            }
        }
        //当第一次请求数据时显示加载框
        if (showLoading) {
            if (!isRefresh && loadingDialog == null) {
                LoadingDialog.showDialogForLoading((Activity) context);

            } else {
                if (loadingDialog != null && isRefresh) {
                    LoadingDialog.showDialogForLoading((Activity) context);
                }
            }
        }

        //默认所有className继承自BaseResp，当为List数据时，为了方便，直接用List里的单个对象
        Class injectClass = BaseResp.class;
        if (BaseResp.class.isAssignableFrom(className) && className != BaseResp.class) {
            injectClass = className;
        }
        RequestCallback callBack = new RequestCallback<Object>() {
            @Override
            public void done(Object hunkResp) {
                handleData(requestCode, hunkResp, saveurl, className, pageIndex, readCache, isArray, toastMessage);
                isRefresh = false;
            }

            @Override
            public void onError(int errorCode, String errorStr) {
                Logs.e("hxq", "errorStr," + errorStr);
                myHandlder.obtainMessage(MSG_ERRO, requestCode, errorCode, errorStr).sendToTarget();
            }
        };
        if (post == 1) {
            EasyHttp.post(context, token, url, injectClass, params, callBack);
        } else if (post == 0) {
            EasyHttp.get(context, token, url, injectClass, params, callBack);
        } else if (post == 2) {
            EasyHttp.put(context, token, url, injectClass, params, callBack);
        } else if (post == 3) {
            EasyHttp.delete(context, token, url, injectClass, params, callBack);
        }

    }


    /**
     * 读取缓存
     *
     * @param saveurl
     * @param requestCode
     */
    private boolean readCache(String saveurl, int requestCode, Class cls, boolean isArray) {
        Object hunkResp = new CommonService(context).getObjectData(DBType.COMMON_DATAS, saveurl);
        if (hunkResp != null) {
            //如果列表的单项类型不正确也重新请求
            if (hunkResp instanceof List) {
                List res = (List) hunkResp;
                if (res.size() > 0) {
                    Object item = res.get(0);
                    if (!cls.isAssignableFrom(item.getClass())) {
                        return false;
                    }
                }
            }
            myHandlder.sendEmptyMessage(MSG_FINISHLOAD);
            myHandlder.obtainMessage(MSG_CACHE, requestCode, 0, hunkResp).sendToTarget();
            cancelLoading();
            return true;
        }
        return false;
    }

    /**
     * 频繁请求提示
     *
     * @param saveurl
     * @return
     */
    private boolean isFrequentRequest(String saveurl, int reqestTime) {
      /*  final String saveLong = saveurl + "_long";
        final String saveInt = saveurl + "_int";
        long lastTime = SUtils.getLongData(context, saveLong);
        if (lastTime == 0) {
            SUtils.saveLongData(context, saveLong, System.currentTimeMillis());
            return false;
        }
        int requestIndex = SUtils.getIntegerData(context, saveInt);
        int divideTime = (int) (System.currentTimeMillis() - lastTime);
        myHandlder.postDelayed(new Runnable() {
            @Override
            public void run() {
                SUtils.saveIntegerData(context, saveInt, 0);
            }
        }, 1000);

        if (requestIndex > 1) {
            //SUtils.makeToast(context, "操作频繁，请稍后重试");
            return true;
        }
        if (divideTime < reqestTime) {
            SUtils.saveLongData(context, saveLong, System.currentTimeMillis());
            SUtils.saveIntegerData(context, saveInt, requestIndex + 1);
            return true;
        }
        SUtils.saveLongData(context, saveLong, System.currentTimeMillis());*/
        return false;
    }

    /**
     * 处理返回数据
     *
     * @param hunkResp
     * @param url
     * @param classD
     */
    private void handleData(final int requestCode, final Object hunkResp, final String url, Class classD, int pageIndex, boolean supportCache) {
        handleData(requestCode, hunkResp, url, classD, pageIndex, supportCache, false, true);
    }

    private void handleData(final int requestCode, final Object hunkResp, final String url, Class classD, int pageIndex, boolean supportCache, boolean isArray, boolean toastMessage) {
        if (hunkResp != null) {
            final BaseResp resp = (BaseResp) hunkResp;
            handleTime = resp.getTime();
            if (resp.getInfo() != null) {
                Object content = resp.getInfo();
                Object datas = null;
                if (!BaseResp.class.isAssignableFrom(classD) && classD != BaseResp.class) {
                    if (content instanceof JSONArray) {
                        JSONArray arrayContent = (JSONArray) content;
                        datas = JSON.parseArray(arrayContent.toJSONString(), classD);
                    } else if (content instanceof JSONObject) {
                        JSONObject arrayContent = (JSONObject) content;
                        datas = JSON.parseObject(arrayContent.toJSONString(), classD);
                    } else if (content instanceof String) {
                        datas = resp;
                    }
                } else {
                    datas = resp;
                }

                if (datas != null) {
                    boolean result = resp.isResult();
                    int code = resp.getError();
                    if (result) {
                        myHandlder.obtainMessage(BaseHelper.MSG_SUCCEED, requestCode, 0, datas).sendToTarget();
                        final Object finalDatas = datas;
                        if (pageIndex == 0 && supportCache) {
                            SThread.getIntances().submit(new Runnable() {
                                @Override
                                public void run() {
                                    new CommonService(context).insert(DBType.COMMON_DATAS, url, finalDatas);
                                }
                            });
                        }

                    } else {
                        if (toastMessage) {
                            //new CodeRespondUtils(context, resp);
                        }
                        myHandlder.obtainMessage(BaseHelper.MSG_ERRO, requestCode, code, resp.getMsg()).sendToTarget();
                    }

                } else {
                    boolean result = resp.isResult();
                    int code = resp.getError();
                    if (result) {
                        myHandlder.obtainMessage(BaseHelper.MSG_SUCCEED, requestCode, -1, null).sendToTarget();
                    } else {
                        if (toastMessage) {
                           // new CodeRespondUtils(context, resp);
                        }
                        myHandlder.obtainMessage(BaseHelper.MSG_ERRO, requestCode, code, resp.getMsg()).sendToTarget();
                    }
                }
            } else {
                int code = resp.getError();
                boolean result = resp.isResult();
                if ((BaseResp.class.isAssignableFrom(classD) || classD == BaseResp.class) && result) {
                    myHandlder.obtainMessage(BaseHelper.MSG_SUCCEED, requestCode, 0, resp).sendToTarget();
                } else {
                    if (result) {
                        if (isArray) {
                            myHandlder.obtainMessage(BaseHelper.MSG_SUCCEED, requestCode, 0, new ArrayList<>()).sendToTarget();
                        } else {
                            if (!BaseResp.class.isAssignableFrom(classD) && classD != BaseResp.class) {
                                myHandlder.obtainMessage(BaseHelper.MSG_ERRO, requestCode, 0, null).sendToTarget();
                            } else {
                                myHandlder.obtainMessage(BaseHelper.MSG_SUCCEED, requestCode, 0, resp).sendToTarget();
                            }
                        }
                    } else {
                        myHandlder.obtainMessage(BaseHelper.MSG_ERRO, requestCode, code, resp.getMsg()).sendToTarget();
                        if (toastMessage) {
                           // new CodeRespondUtils(context, resp);
                        }
                    }
                }
            }
        }
        myHandlder.sendEmptyMessage(MSG_FINISHLOAD);
    }

    public void cancelLoading() {
        if (loadingDialog != null) {
            loadingDialog.cancelDialogForLoading();
        }
    }

    /**
     * 设置沉浸式状态栏
     */
    public void setLayoutFullscreen(boolean fullscreen) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            Activity activity = (Activity) context;
            Window window = activity.getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (fullscreen) {
                    window.getDecorView()
                            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

                } else {
                    window.getDecorView()
                            .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                View view = activity.findViewById(R.id.title);
                if (view != null) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    params.height = SUtils.getDip(context, 45) + SUtils.getStatusBarHeight(activity);
                }

            } else {
                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    /**
     * 处理RecyleView的数据，支持上拉加载显示
     *
     * @param obj
     * @param sRecycleView
     * @param pageIndex
     */
    public void handleViewData(Object obj, SmartRecyclerView sRecycleView, int pageIndex) {
        handleViewData(obj, sRecycleView, pageIndex, 0);
    }

    /**
     * 处理RecyleView的数据，支持上拉加载显示
     *
     * @param obj
     * @param sRecycleView
     * @param pageIndex
     * @param otherItemCount obj里除了正常一页的的数据类型之外的数据数量，例如有时候会插入广告数据
     */
    public void handleViewData(Object obj, SmartRecyclerView sRecycleView, int pageIndex, int otherItemCount) {
        if (sRecycleView == null) {
            return;
        }
        SRecycleMoreAdapter adapter = (SRecycleMoreAdapter) sRecycleView.getRefreshViewForTypeRecycleView().getAdapter();
        if (adapter == null) {
            return;
        }
        if (obj != null) {
            List resp = (List) obj;

            int size = resp.size() - otherItemCount;
            Logs.i("pageIndex:" + pageIndex);
            if (size > 0) {
                if (pageIndex > 0 && adapter.items != null) {
                    adapter.items.addAll(resp);
                } else {
                    adapter.items = resp;
                }
                //如果当前加载的数据量小于设定的单页数量，则显示底部栏
                Logs.i("size<:::" + (size < loadCount));
                if (size < loadCount) {
                    //sRecycleView.setPullUpRefreshable(false);
                    adapter.notifyDataChanged(adapter.items, false);
                } else {
                   // sRecycleView.setPullUpRefreshable(true);
                    adapter.notifyDataChanged(adapter.items, true);
                }
                //sRecycleView.hideEmptyView();
                //如果不是第一页，且没有更多数据了，显示底部栏
            } else if (pageIndex > 0) {
                //sRecycleView.setPullUpRefreshable(false);
                adapter.setBottomViewVisible();
            } else {
                adapter.notifyDataChanged(new ArrayList<>());
                if (sRecycleView != null && adapter.getHeaderCount() > 0) {
                    adapter.showSRecycleEmptyView();
                } else {
                    adapter.showEmptyView();
                    //sRecycleView.showEmptyView();
                }
            }
        } else {
            adapter.notifyDataChanged(new ArrayList<>());
            adapter.showEmptyView();
            //sRecycleView.showEmptyView();
        }
    }


    public long getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(long handleTime) {
        this.handleTime = handleTime;
    }

    public int getLoadCount() {
        return loadCount;
    }

    public void setLoadCount(int loadCount) {
        this.loadCount = loadCount;
    }

    public boolean isShowLoading() {
        return showLoading;
    }

    public void setShowLoading(boolean showLoading) {
        this.showLoading = showLoading;
    }

    public void setIsRefresh(boolean isRefresh) {
        this.isRefresh = isRefresh;
    }
}
