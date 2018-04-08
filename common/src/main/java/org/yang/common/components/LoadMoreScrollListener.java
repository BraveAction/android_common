package org.yang.common.components;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * 针对
 * Created by Gxy on 2017/4/20
 */
public class LoadMoreScrollListener extends RecyclerView.OnScrollListener {

    private CallBack callBack;


    public LoadMoreScrollListener(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            LinearLayoutManager linearLayout = ((LinearLayoutManager) recyclerView.getLayoutManager());
            int lastVisibleItemPosition = linearLayout.findLastCompletelyVisibleItemPosition() + 1;
            int itemCount = recyclerView.getAdapter().getItemCount();
            if (lastVisibleItemPosition == itemCount) {
                if (callBack != null) {
                    callBack.onLoadMore();
                }
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
    }

    public interface CallBack {
        void onLoadMore();
    }
}
