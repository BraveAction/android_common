package org.yang.common.components;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by Gxy on 2017/5/25
 */

public interface OnRecyclerViewLoadListener {
    void onRefresh(PtrFrameLayout frame);

    void onLoadMore(PtrFrameLayout frame);

    enum PullOperation {
        REFRESH(1),

        LOADMORE(2);

        final int type;

        PullOperation(int type) {
            this.type = type;
        }
    }
}