package org.yang.common.recyclerView;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.MotionEvent;
import android.view.View;

import com.alibaba.android.vlayout.VirtualLayoutManager;

import org.yang.common.components.BaseRefreshLayout;
import org.yang.common.components.OnRecyclerViewLoadListener;
import org.yang.common.components.RecyclerViewPtrBaseHandler;
import org.yang.common.components.RecyclerViewPtrlHandler;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by Gxy on 2017/4/26
 */

public class RecyclerViewUtils {
    public static void setBaseProperties(RecyclerView recyclerView) {
        RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();
        recycledViewPool.setMaxRecycledViews(0, 10);
        recyclerView.setRecycledViewPool(recycledViewPool);
        recyclerView.setHasFixedSize(true);
        SimpleItemAnimator simpleItemAnimator = (SimpleItemAnimator) recyclerView.getItemAnimator();
        simpleItemAnimator.setSupportsChangeAnimations(false);
        simpleItemAnimator.setRemoveDuration(0);

        //解决item中有editText时自动滚动的问题
        recyclerView.setDescendantFocusability(RecyclerView.FOCUS_BEFORE_DESCENDANTS);
        recyclerView.setFocusable(true);
        recyclerView.setFocusableInTouchMode(true);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                return false;
            }
        });
    }

    public static VirtualLayoutManager setVLayout(RecyclerView recyclerView) {
        VirtualLayoutManager virtualLayoutManager = new VirtualLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(virtualLayoutManager);
        return virtualLayoutManager;
    }

    /**
     * 为RecyclerView设置上拉加载下拉刷新
     * 是否有上拉加载由mPtrFrame的类型决定
     *
     * @param mRecyclerView
     * @param mPtrFrame
     * @param onRecyclerViewLoadListener
     */
    public static void setRecyclerViewPtrl(RecyclerView mRecyclerView, PtrFrameLayout mPtrFrame, OnRecyclerViewLoadListener onRecyclerViewLoadListener) {
//        mPtrFrame.setLastUpdateTimeRelateObject(this);
        PtrDefaultHandler ptrDefaultHandler;
        if (mPtrFrame instanceof PtrClassicFrameLayout) {
            ptrDefaultHandler = new RecyclerViewPtrlHandler(mRecyclerView, onRecyclerViewLoadListener);
        } else {
            ptrDefaultHandler = new RecyclerViewPtrBaseHandler(mRecyclerView, onRecyclerViewLoadListener);
        }
        mPtrFrame.setPtrHandler(ptrDefaultHandler);
        // the following are default settings
        mPtrFrame.setResistance(1.7f); // you can also set foot and header separately
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToCloseFooter(0); // footer will hide immediately when completed
        mPtrFrame.setForceBackWhenComplete(true);
        mPtrFrame.setDurationToClose(1000);  // you can also set foot and header separately
        // default is false
//        mPtrFrame.setPullToRefresh(false);

        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
    }


    public static void setRecyclerViewRefresh(RecyclerView mRecyclerView, BaseRefreshLayout mPtrFrame, OnRecyclerViewLoadListener onRecyclerViewLoadListener) {
//        mPtrFrame.setLastUpdateTimeRelateObject(this);
        PtrDefaultHandler ptrDefaultHandler;
        if (mPtrFrame instanceof BaseRefreshLayout) {
            ptrDefaultHandler = new RecyclerViewPtrlHandler(mRecyclerView, onRecyclerViewLoadListener);
        } else {
            ptrDefaultHandler = new RecyclerViewPtrBaseHandler(mRecyclerView, onRecyclerViewLoadListener);
        }
        mPtrFrame.setPtrHandler(ptrDefaultHandler);
        // the following are default settings
        mPtrFrame.setResistance(1.7f); // you can also set foot and header separately
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToCloseFooter(0); // footer will hide immediately when completed
        mPtrFrame.setForceBackWhenComplete(true);
        mPtrFrame.setDurationToClose(1000);  // you can also set foot and header separately
        // default is false
//        mPtrFrame.setPullToRefresh(false);

        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
    }

}
