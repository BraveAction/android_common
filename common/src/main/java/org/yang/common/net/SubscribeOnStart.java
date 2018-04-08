package org.yang.common.net;

import org.yang.common.base.NetRequestHelper;

import org.reactivestreams.Subscription;

import io.reactivex.annotations.NonNull;

/**
 * Created by Gxy on 2017/7/20
 */

public final class SubscribeOnStart extends BaseConsumer<Subscription> {

    public SubscribeOnStart(NetRequestHelper netRequestHelper, EPType EPType) {
        super(netRequestHelper, EPType);
    }

    @Override
    public void accept(@NonNull Subscription subscription) throws Exception {
        if (mEPType != EPType.SILENT) {
            mNetRequestHelper.showProgressDialog();
        }
    }

}
