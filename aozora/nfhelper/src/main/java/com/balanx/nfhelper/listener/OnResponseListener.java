package com.balanx.nfhelper.listener;

/**
 * Created by dell on 2017/7/4.
 */

public interface OnResponseListener {

    void succeed(String url);

    void failure();
}
