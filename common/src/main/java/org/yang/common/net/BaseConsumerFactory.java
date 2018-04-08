package org.yang.common.net;

import org.yang.common.base.NetRequestHelper;

/**
 * Created by Gxy on 2017/7/22
 */

public final class BaseConsumerFactory {
    private static final BaseConsumerFactory ourInstance = new BaseConsumerFactory();
    public static NetRequestHelper mNetRequestHelper;
    private static EPType mEPType;

    private BaseConsumerFactory() {

    }

    public static BaseConsumerFactory getInstance(NetRequestHelper netRequestHelper, EPType ePType) {
        mNetRequestHelper = netRequestHelper;
        mEPType = ePType;

        return ourInstance;
    }

    public BaseConsumer create(int consumerType) {
        switch (consumerType) {
            case BaseConsumer.START:
                return new SubscribeOnStart(mNetRequestHelper, mEPType);
            case BaseConsumer.NEXT:
                return new SubscribeOnNext(mNetRequestHelper, mEPType);
            case BaseConsumer.ERROR:
                return new SubscribeOnError(mNetRequestHelper, mEPType);
        }
        return new SubscribeOnError(mNetRequestHelper, mEPType);
    }

}
