package org.yang.common.base;

import android.support.annotation.StringRes;

import org.yang.common.net.EPType;

/**
 * 网络请求辅助类
 * Created by Gxy on 2017/4/19
 */

public interface NetRequestHelper {

    /**
     * Toast显示提示
     *
     * @param errorMsg
     */
    void showMessage(String errorMsg);

    void showMessage(@StringRes int errorMsgRes);

    /**
     * 当请求失败时调用此方法更新视图
     *
     * @param EPType   异常类型
     * @param errorMsg 异常信息
     * @return`1
     */
    void onHandleException(EPType EPType, int errorMsg);

    /**
     * 显示数据加载对话框
     */
    void showProgressDialog();

    /**
     * 隐藏数据加载对话框
     */
    void hideProgressDialog();
}
