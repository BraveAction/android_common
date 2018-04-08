package org.yang.common.net;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Gxy on 2017/4/19
 */

public class CacheInterceptor implements Interceptor {
    String maxAge = "max-age=" + (60 * 2);
//    3600 * 24 * 30//缓存30天

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        Response response1 = response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", maxAge)
                .build();

        return response1;
    }
}