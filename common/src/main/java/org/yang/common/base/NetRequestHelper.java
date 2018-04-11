package org.yang.common.base;

import android.support.annotation.StringRes;

import org.yang.common.net.EPType;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * 网络请求辅助类
 * Created by Gxy on 2017/4/19
 */

public interface NetRequestHelper {
    CompositeDisposable mComposite = new CompositeDisposable();

    /**
     * 添加网络处理的流
     *
     * @param disposable
     */
    void add(Disposable disposable);

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
