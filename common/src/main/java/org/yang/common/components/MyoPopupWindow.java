package org.yang.common.components;

import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

/**
 * 解决andriod7.0下用户弹出的PopupWindow会覆盖当前的视窗而覆盖整个手机屏幕,并不是在anchorView的下方弹出来的问题
 * Created by Gxy on 2017/5/11
 */

public class MyoPopupWindow extends PopupWindow {

    public MyoPopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    @Override
    public void showAsDropDown(View anchorView, int xoff, int yoff) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
            int[] a = new int[2];
            anchorView.getLocationInWindow(a);
            showAtLocation(anchorView, Gravity.NO_GRAVITY, xoff, a[1] + anchorView.getHeight() + yoff);
        } else {
            super.showAsDropDown(anchorView, xoff, yoff);
        }
    }

    @Override
    public void showAsDropDown(View anchorView) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
            int[] a = new int[2];
            anchorView.getLocationInWindow(a);
            showAtLocation(anchorView, Gravity.NO_GRAVITY, 0, a[1] + anchorView.getHeight() + 0);
        } else {
            super.showAsDropDown(anchorView);
        }
    }
}
