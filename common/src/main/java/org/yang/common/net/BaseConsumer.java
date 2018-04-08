package org.yang.common.net;


import org.yang.common.base.NetRequestHelper;

import io.reactivex.functions.Consumer;

/**
 * Created by Gxy on 2017/7/20
 */

public abstract class BaseConsumer<T> implements Consumer<T> {
    public final static int START = 0;
    public final static int NEXT = 1;
    public final static int ERROR = 2;
    protected NetRequestHelper mNetRequestHelper;
    protected EPType mEPType;

    public BaseConsumer(NetRequestHelper netRequestHelper, EPType EPType) {
        this.mNetRequestHelper = netRequestHelper;
        this.mEPType = EPType;
    }

    public NetRequestHelper getNetRequestHelper() {
        return mNetRequestHelper;
    }

    public void setNetRequestHelper(NetRequestHelper netRequestHelper) {
        this.mNetRequestHelper = netRequestHelper;
    }

    public EPType getErrorType() {
        return mEPType;
    }

    public void setErrorType(EPType EPType) {
        this.mEPType = EPType;
    }


}
