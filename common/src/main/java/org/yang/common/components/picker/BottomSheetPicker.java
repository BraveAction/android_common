package org.yang.common.components.picker;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Gxy on 2017/5/20
 */

public abstract class BottomSheetPicker {
    protected Context mContext;
    private BottomSheetDialog mBottomSheetDialog;
    private int height = 200;

    public BottomSheetPicker(Context mContext) {
        this(mContext, false);
    }

    public BottomSheetPicker(Context mContext, boolean initViewable) {
        this.mContext = mContext;

        if (initViewable) {
            initView();
            initDialog();
        }
    }

    public abstract void initView();


    public void show() {
        if (mBottomSheetDialog != null) {
            mBottomSheetDialog.show();
            mBottomSheetDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, getHeight());
            mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        }
    }

    private int getDefaultHeight() {
        if (mContext != null) {
            this.height = this.mContext.getResources().getDisplayMetrics().heightPixels / 2;
        }
        return this.height;
    }

    private int getHeight() {
        return getDefaultHeight();
    }

    public void setHeight(int height) {
        if (height != 0) {
            this.height = height;
        } else {      //默认高
            this.height = getDefaultHeight();
        }
    }

    public abstract ViewDataBinding getBinding();

    /**
     * 初始化底部对话框
     */
    protected void initDialog() {
        int dialogHeight = getHeight();
        ViewDataBinding binding = getBinding();
        if (binding != null) {
            View contentView = binding.getRoot();
            mBottomSheetDialog = new BottomSheetDialog(mContext);

            mBottomSheetDialog.setContentView(contentView);
            mBottomSheetDialog.setCancelable(false);

            contentView.setMinimumHeight(dialogHeight);
            View view = mBottomSheetDialog.getWindow().findViewById(android.support.design.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(view).setPeekHeight(dialogHeight);
        }
    }


    public void dismiss() {
        if (mBottomSheetDialog != null) {
            mBottomSheetDialog.dismiss();
        }
    }

}
