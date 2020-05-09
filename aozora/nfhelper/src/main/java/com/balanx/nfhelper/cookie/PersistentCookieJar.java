package com.balanx.nfhelper.cookie;

import android.text.TextUtils;

import com.balanx.nfhelper.server.PostData;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.cache.CookieCache;
import com.franmontiel.persistentcookiejar.persistence.CookiePersistor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by xiastars on 2017/11/13.
 */

public class PersistentCookieJar implements ClearableCookieJar {
    private CookieCache cache;
    private CookiePersistor persistor;

    public PersistentCookieJar(CookieCache cache, CookiePersistor persistor) {
        this.cache = cache;
        this.persistor = persistor;
        this.cache.addAll(persistor.loadAll());
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        try{
            this.cache.addAll(cookies);
            this.persistor.saveAll(cookies);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        ArrayList validCookies = new ArrayList();
        Iterator it = this.cache.iterator();
        while (it.hasNext()) {
            Cookie currentCookie = (Cookie) it.next();
            validCookies.add(currentCookie);
            String name = currentCookie.name();
            String value = currentCookie.value();
            if (!TextUtils.isEmpty(name)) {
                if (name.equals("SESSION")) {
                    PostData.SESSION = value;
                }
            }
        }
        return validCookies;
        /*ArrayList removedCookies = new ArrayList();
        ArrayList validCookies = new ArrayList();
        Iterator it = this.cache.iterator();

        while(it.hasNext()) {
            Cookie currentCookie = (Cookie)it.next();
            if(isCookieExpired(currentCookie)) {
                removedCookies.add(currentCookie);
                it.remove();
            } else if(currentCookie.matches(url)) {
                validCookies.add(currentCookie);
                String name = currentCookie.name();
                String value = currentCookie.value();
                if (!TextUtils.isEmpty(name)) {
                    if (name.equals("SESSION")) {
                        PostData.SESSION = value;
                    }
                }
            }
        }

        this.persistor.removeAll(removedCookies);
        return validCookies;*/
    }

    private static boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }

    @Override
    public void clearSession() {

    }

    @Override
    public synchronized void clear() {
        this.cache.clear();
        this.persistor.clear();
    }
}
