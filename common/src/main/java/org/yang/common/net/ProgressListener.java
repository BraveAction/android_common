package org.yang.common.net;

/**
 * Created by Gxy on 2017/6/16
 */

public interface ProgressListener {
    /**
     * @param currentBytes  已下载字节数
     * @param contentLength 总字节数
     * @param done          是否下载完成
     */
    void onProgress(long currentBytes, long contentLength, boolean done);
}
