package org.yang.common.base;

import org.yang.common.BasePresenter;
import org.yang.common.base.NetRequestHelper;
import org.yang.common.net.EPType;
import org.yang.common.net.RetrofitClient;
import org.yang.common.net.RxSchedules;
import org.yang.common.net.SubscribeOnError;
import org.yang.common.net.SubscribeOnNext;
import org.yang.common.net.SubscribeOnStart;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
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
    protected CompositeDisposable mComposite = new CompositeDisposable();
    protected NetRequestHelper mNetRequestHelper;

    public ReRxPresenter(NetRequestHelper netRequestHelper) {
        this.mNetRequestHelper = netRequestHelper;
    }

    @Override
    public void subscribe() {

    }

    private Flowable doInMainThread(Flowable flowable, EPType epType) {
        return flowable.
                compose(RxSchedules.mainThread()).
                doOnSubscribe(new SubscribeOnStart(mNetRequestHelper, epType)).
                doOnNext(new SubscribeOnNext(mNetRequestHelper, epType)).
                doOnComplete(() -> mNetRequestHelper.hideProgressDialog()).
                doOnError(new SubscribeOnError(mNetRequestHelper, epType));
    }

    protected <T> void doRequest(Flowable<T> flowable, EPType epType, DisposableSubscriber<T> subscriber) {
        mComposite.add((Disposable) doInMainThread(flowable, epType).subscribeWith(subscriber));
    }

    protected <T> void doRequest(Flowable<T> flowable, EPType epType, ResourceSubscriber<T> subscriber) {
        mComposite.add((Disposable) doInMainThread(flowable, epType).subscribeWith(subscriber));
    }

    protected <T extends BaseResponse> void doRequest(Flowable<T> flowable, EPType epType, Consumer<T> onNext) {
        mComposite.add(doInMainThread(flowable, epType).subscribe(onNext, new SubscribeOnError(mNetRequestHelper, epType)));
    }

    protected <T extends BaseResponse> void doRequest(Flowable<T> flowable, EPType epType, Consumer<T> onNext, Consumer<Throwable> onError) {
        mComposite.add(doInMainThread(flowable, epType).subscribe(onNext, onError));
    }

    protected <T extends BaseResponse> void doRequest(Flowable<T> flowable, EPType epType, Consumer<T> onNext, Consumer<Throwable> onError, Action onComplete) {
        mComposite.add(doInMainThread(flowable, epType).subscribe(onNext, onError, onComplete));
    }

    @Override
    public void unsubscribe() {
        if (mComposite != null) {
            mComposite.clear();
        }
    }
}
