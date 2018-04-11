package org.yang.common.net;

import android.text.TextUtils;

import org.yang.common.base.BaseResponse;
import org.yang.common.base.NetRequestHelper;

import io.reactivex.annotations.NonNull;

/**
 * Created by Gxy on 2017/7/20
 */

public final class SubscribeOnNext<T extends BaseResponse> extends BaseConsumer<T> {
    protected SubscribeOnNext(NetRequestHelper netRequestHelper, EPType EPType) {
        super(netRequestHelper, EPType);
    }

    @Override
    public void accept(@NonNull T response) throws Exception {
        String hint = response.getSuccessMessage();
        if (!TextUtils.isEmpty(hint) && hint != "null") {
            mNetRequestHelper.hideProgressDialog();
            if (mEPType != EPType.SILENT && mEPType != EPType.PAGE) {
                mNetRequestHelper.showMessage(hint);
            }
        }
    }
}
