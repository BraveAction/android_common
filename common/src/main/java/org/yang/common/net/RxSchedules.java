package org.yang.common.net;

import io.reactivex.FlowableTransformer;

/**
 * Created by Gxy on 2017/7/12
 */

public final class RxSchedules {
    public static FlowableTransformer mainThread() {
        return new FlowableTransformerOnBackground();
    }
}
