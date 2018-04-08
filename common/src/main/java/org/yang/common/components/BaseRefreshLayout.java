package org.yang.common.components;

import android.content.Context;
import android.util.AttributeSet;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrFrameLayout;


/**
 * 只有下拉刷新
 */
public class BaseRefreshLayout extends PtrFrameLayout {

    /**
     * HeaderView
     */
    private PtrClassicDefaultHeader mHeaderView;

    public BaseRefreshLayout(Context context) {
        this(context, null);
    }

    public BaseRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initView();
    }


    /**
     * 初始化view
     */
    private void initView() {
        mHeaderView = new PtrClassicDefaultHeader(getContext());
        setHeaderView(mHeaderView);
        addPtrUIHandler(mHeaderView);
    }

}
