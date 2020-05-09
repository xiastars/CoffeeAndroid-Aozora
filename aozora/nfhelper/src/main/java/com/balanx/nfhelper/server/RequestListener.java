package com.balanx.nfhelper.server;

/**
 * 发起访问请求时所需的回调接口。
 */

public interface RequestListener {

    void onComplete(String response);

    void onErrorException(SummerException e);
}
