package org.yang.common.net;

import org.reactivestreams.Subscription;
import org.yang.common.base.NetRequestHelper;

import io.reactivex.annotations.NonNull;

/**
 * Created by Gxy on 2017/7/20
 */

public final class SubscribeOnStart extends BaseConsumer<Subscription> {

    protected SubscribeOnStart(NetRequestHelper netRequestHelper, EPType EPType) {
        super(netRequestHelper, EPType);
    }

    @Override
    public void accept(@NonNull Subscription subscription) {
        if (mEPType != EPType.SILENT) {
            mNetRequestHelper.showProgressDialog();
        }
    }

}
