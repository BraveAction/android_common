package org.yang.common.base;

import org.yang.common.net.BaseConsumerFactory;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;

/**
 * 界面接口
 * Created by Gxy on 2017/4/8
 */

public interface BaseWindow {


    /**
     * 发送请求,此接口在fragment中使用(必须实现)
     *
     * @param flowable
     * @param subscriber
     * @param baseConsumerFactory
     */
    void doRequest(Flowable flowable, FlowableSubscriber subscriber, BaseConsumerFactory baseConsumerFactory);

    /**
     * 加载通用视图
     */
    void initCommonViews();

    /**
     * 初始控件 ，设置监听事件
     */
    void inflateViews();

}
