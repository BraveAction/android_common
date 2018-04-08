package org.yang.common.net;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Gxy on 2017/4/5
 * 后台线程执行同步，主线程执行异步操作
 * 并且拦截所有错误，不让app崩溃
 */

public class FlowableTransformerOnBackground implements FlowableTransformer {
    @Override
    public Publisher apply(@NonNull Flowable upstream) {
        return upstream.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
