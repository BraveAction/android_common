package org.yang.common.components;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by Gxy on 2017/4/20
 */
public abstract class TmallRecyclerViewPtrHandler extends PtrDefaultHandler {

    private RecyclerView mRecyclerView;

    public TmallRecyclerViewPtrHandler(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        if (mRecyclerView.getChildCount() == 0) {
            return true;
        }
        int top = mRecyclerView.getChildAt(0).getTop();
        if (top != 0) {
            return false;
        }
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int position = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
            if (position == 0) {
                return true;
            } else if (position == -1) {
                position = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                return position == 0;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            boolean allViewAreOverScreen = true;
            int[] positions = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
            for (int i = 0; i < positions.length; i++) {
                if (positions[i] == 0) {
                    return true;
                }
                if (positions[i] != -1) {
                    allViewAreOverScreen = false;
                }
            }
            if (allViewAreOverScreen) {
                positions = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
                for (int i = 0; i < positions.length; i++) {
                    if (positions[i] == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


}
