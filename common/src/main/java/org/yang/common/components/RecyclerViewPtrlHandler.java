package org.yang.common.components;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * 下拉刷新上拉加载
 * Created by Gxy on 2017/4/20
 */
public class RecyclerViewPtrlHandler extends PtrDefaultHandler2 {

    private RecyclerView mRecyclerView;
    private OnRecyclerViewLoadListener mOnRecyclerViewLoadListener;

    public RecyclerViewPtrlHandler(RecyclerView mRecyclerView) {
        this.mRecyclerView = mRecyclerView;
    }

    public RecyclerViewPtrlHandler(RecyclerView mRecyclerView, OnRecyclerViewLoadListener mOnRecyclerViewLoadListener) {
        this.mRecyclerView = mRecyclerView;
        this.mOnRecyclerViewLoadListener = mOnRecyclerViewLoadListener;
    }

    @Override
    public void onLoadMoreBegin(PtrFrameLayout frame) {
        if (mOnRecyclerViewLoadListener != null) {
            mOnRecyclerViewLoadListener.onLoadMore(frame);
        }
    }

    @Override
    public void onRefreshBegin(PtrFrameLayout frame) {
        if (mOnRecyclerViewLoadListener != null) {
            mOnRecyclerViewLoadListener.onRefresh(frame);
        }
    }

    @Override
    public boolean checkCanDoLoadMore(PtrFrameLayout frame, View content, View footer) {

        int newState = mRecyclerView.getScrollState();
        LinearLayoutManager lm = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter == null) {
            return false;
        }
        int totalItemCount = adapter.getItemCount();
        int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
        int visibleItemCount = mRecyclerView.getChildCount();

        if (newState == RecyclerView.SCROLL_STATE_IDLE
                && lastVisibleItemPosition == totalItemCount - 1
                && visibleItemCount > 0) {
            //加载更多
            return true && !checkCanDoRefresh(frame, content, footer);
        }
        return false;
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
