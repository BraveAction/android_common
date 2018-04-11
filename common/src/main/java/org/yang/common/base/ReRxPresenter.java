package org.yang.common.base;

import org.yang.common.BasePresenter;
import org.yang.common.net.BaseConsumer;
import org.yang.common.net.BaseConsumerFactory;
import org.yang.common.net.EPType;
import org.yang.common.net.RetrofitClient;
import org.yang.common.net.RxSchedules;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.subscribers.DisposableSubscriber;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * 特性:
 * 1:事件的订阅和取消(RxAndroid/RxJave)
 * 2:提供给子类网络接口服务(Retrofit)
 * Created by Gxy on 2017/7/19
 */

public abstract class ReRxPresenter<S> implements BasePresenter {
    protected S mApiServices = (S) RetrofitClient.getInstance().getServices();
    private NetRequestHelper mNetRequestHelper;

    public ReRxPresenter(NetRequestHelper netRequestHelper) {
        this.mNetRequestHelper = netRequestHelper;
    }

    @Override
    public void subscribe() {

    }

    @Override
    public void unsubscribe() {

    }

    public Flowable doInMainThread(Flowable flowable, EPType epType) {
        BaseConsumerFactory baseConsumerFactory = BaseConsumerFactory.getInstance(mNetRequestHelper, epType);
        return flowable.compose(RxSchedules.mainThread()).
                doOnSubscribe(baseConsumerFactory.create(BaseConsumer.START)).
                doOnNext(baseConsumerFactory.create(BaseConsumer.NEXT)).
                doOnComplete(() -> mNetRequestHelper.hideProgressDialog()).
                doOnError(baseConsumerFactory.create(BaseConsumer.ERROR));
    }

    public <T> void doRequest(Flowable<T> flowable, EPType epType, DisposableSubscriber<T> subscriber) {
        BaseConsumerFactory.getInstance(mNetRequestHelper, epType);
        mNetRequestHelper.add((Disposable) doInMainThread(flowable, epType).subscribeWith(subscriber));
    }

    public <T> void doRequest(Flowable<T> flowable, EPType epType, ResourceSubscriber<T> subscriber) {
        mNetRequestHelper.add((Disposable) doInMainThread(flowable, epType).subscribeWith(subscriber));
    }

    public <T extends BaseResponse> void doRequest(Flowable<T> flowable, EPType epType, Consumer<T> onNext) {
        mNetRequestHelper.add(doInMainThread(flowable, epType).subscribe(onNext));
    }

    public <T extends BaseResponse> void doRequest(Flowable<T> flowable, EPType epType, Consumer<T> onNext, Consumer<Throwable> onError) {
        mNetRequestHelper.add(doInMainThread(flowable, epType).subscribe(onNext, onError));
    }

    public <T extends BaseResponse> void doRequest(Flowable<T> flowable, EPType epType, Consumer<T> onNext, Consumer<Throwable> onError, Action onComplete) {
        mNetRequestHelper.add(doInMainThread(flowable, epType).subscribe(onNext, onError, onComplete));
    }


}
