package org.yang.common.net;

import org.yang.common.R;
import org.yang.common.base.NetRequestHelper;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.annotations.NonNull;
import retrofit2.HttpException;

/**
 * Created by Gxy on 2017/4/19
 */
public class SubscribeOnError extends BaseConsumer<Throwable> {

    protected SubscribeOnError(NetRequestHelper netRequestHelper, EPType EPType) {
        super(netRequestHelper, EPType);
    }

    @Override
    public void accept(@NonNull Throwable throwable) {
        throwable.printStackTrace();

        int errorRes = R.string.myException;
        if (throwable instanceof UnknownHostException) {
            errorRes = R.string.theSpecifiedException;
        } else if (throwable instanceof SocketTimeoutException) {
            errorRes = R.string.theSpecifiedException;
        } else if (throwable instanceof HttpException) {
            errorRes = R.string.theSpecifiedException;
        } else if (throwable instanceof ConnectException) {
            errorRes = R.string.theSpecifiedException;
        }

        if (mNetRequestHelper != null) {
            mNetRequestHelper.hideProgressDialog();
            if (mEPType != null) {
                mNetRequestHelper.onHandleException(mEPType, errorRes);
            }
        }

    }

}
