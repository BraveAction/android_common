package org.yang.common.net;

import java.io.EOFException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okio.Buffer;

/**
 * 令牌拦截器,用于在请求中添加特定,统一的头和参数
 * Created by Gxy on 2017/4/24
 */
public abstract class TokenInterceptor implements Interceptor {
    /**
     * 添加公共参数
     *
     * @param oldRequest
     * @return
     */
    public abstract Request addSignParameters(Request oldRequest);

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     *
     * @param buffer
     * @return 可读的二进制文本
     */
    protected boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}
