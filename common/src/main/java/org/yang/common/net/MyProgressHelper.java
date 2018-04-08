package org.yang.common.net;

import okhttp3.RequestBody;

/**
 * Created by Gxy on 2017/6/5
 */

public class MyProgressHelper {
    /**
     * 包装请求体用于上传文件的回调
     *
     * @param requestBody             请求体RequestBody
     * @param progressRequestListener 进度回调接口
     * @return 包装后的进度回调请求体
     */
    public static ProgressRequestBody addProgressRequestListener(RequestBody requestBody, ProgressListener progressRequestListener) {
        //包装请求体
        return new ProgressRequestBody(requestBody, progressRequestListener);
    }
}
