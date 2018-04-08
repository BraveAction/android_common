package org.yang.common.net;

/**
 * 网络请求回显异常展示类别(ECHO processing type)
 * Created by Gxy on 2017/4/18
 */
public enum EPType {

    SILENT(1),      //异常静默处理,界面一般为列表且有缓存

    PAGE(2),        //异常发生时显示对应的异常界面

    TOAST(3),       //通过Toast提示异常信息

    DIALOG(4);      //通过对话框提示异常信息

    final int type;

    EPType(int type) {
        this.type = type;
    }
}
