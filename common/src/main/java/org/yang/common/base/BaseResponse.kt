package org.yang.common.base

/**
 *
 * Created by Gxy on 2017/7/4
 */
abstract class BaseResponse {
    /**
     * 用户操作未成功,获取提示信息
     */
    abstract fun getNoExpectedResponseHint(): String?

    abstract fun getSuccessMessage(): String?

}