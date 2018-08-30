package org.yang.common.fragment;

import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.yang.common.R;
import org.yang.common.components.OnRecyclerViewLoadListener;
import org.yang.common.net.EPType;
import org.yang.common.recyclerView.BaseAdapter;
import org.yang.common.recyclerView.RecyclerViewEmptySupport;
import org.yang.common.recyclerView.RecyclerViewUtils;

import java.util.List;

import in.srain.cube.views.ptr.PtrFrameLayout;
import io.reactivex.Flowable;
import io.reactivex.subscribers.DisposableSubscriber;


public abstract class BaseRecyclerViewFragment<VB extends ViewDataBinding, RESP> extends BaseLazyFragment<VB> implements OnRecyclerViewLoadListener {
    protected RecyclerViewEmptySupport mRecyclerView;
    protected BaseAdapter mAdapter;
    protected int mCurrentPage = 1;
    protected PtrFrameLayout mPtrFrameLayout;
    private View mErrorView;

    @Override
    public void fetchData() {
        onRefresh(mPtrFrameLayout);
    }

    protected
    @LayoutRes
    int getLayoutId() {
        return R.layout.fragment_ptrl_recyclerview;
    }

    @Override
    public void inflateViews() {
        mRecyclerView = mRootLayout.findViewById(R.id.recyclerView);
        RecyclerViewUtils.setBaseProperties(mRecyclerView);
        mPtrFrameLayout = mRootLayout.findViewById(R.id.refreshLayout);
        if (mPtrFrameLayout != null) {
            RecyclerViewUtils.setRecyclerViewPtrl(mRecyclerView, mPtrFrameLayout, this);
        }
        mErrorView = mRootLayout.findViewById(R.id.errorLayout);
        if (mErrorView != null) {
            mRecyclerView.setEmptyView(mErrorView);
        }

        mRecyclerView.setLayoutManager(getLayoutManager());

        mAdapter = getAdapter();
        mRecyclerView.setAdapter(mAdapter.getAdapter());
    }

    /**
     * 获取列表布局管理器
     *
     * @return
     */
    protected abstract RecyclerView.LayoutManager getLayoutManager();

    /**
     * 获取列表适配器
     *
     * @return
     */
    protected abstract BaseAdapter getAdapter();


    /**
     * 获取请求参数
     *
     * @param operation
     * @return
     */
    protected abstract Flowable getRequestParameter(PullOperation operation);


    @Override
    public void onRefresh(PtrFrameLayout frame) {

        mCurrentPage = 1;
        Flowable flowable = getRequestParameter(PullOperation.REFRESH);
        if (flowable == null) {
            mPtrFrameLayout.refreshComplete();

            return;
        }
        DisposableSubscriber flowableSubscriber = new DisposableSubscriber<RESP>() {

            @Override
            public void onNext(RESP sendGoodOrder) {
                List list = packageData(sendGoodOrder);
                if (list != null) {
                    mAdapter.setData(list);
                }
            }

            @Override
            public void onError(Throwable t) {
                if (mPtrFrameLayout != null) {
                    mPtrFrameLayout.refreshComplete();
                }
            }

            @Override
            public void onComplete() {
                if (mPtrFrameLayout != null) {
                    mPtrFrameLayout.refreshComplete();
                }
            }
        };

        doRequest(flowable, flowableSubscriber, EPType.PAGE);

    }


    /**
     * 封装结果
     */
    protected abstract List packageData(RESP object);


    @Override
    public void onLoadMore(PtrFrameLayout frame) {
        Flowable flowable = getRequestParameter(PullOperation.LOADMORE);
        if (flowable == null) {
            return;
        }
        DisposableSubscriber flowableSubscriber = new DisposableSubscriber<RESP>() {

            @Override
            public void onNext(RESP sendGoodOrder) {
                List list = packageData(sendGoodOrder);
                if (list.size() > 0) {
                    mAdapter.addData(list);
                }
            }

            @Override
            public void onError(Throwable t) {
                if (mPtrFrameLayout != null) {
                    mPtrFrameLayout.refreshComplete();
                }
            }

            @Override
            public void onComplete() {
                if (mPtrFrameLayout != null) {
                    mPtrFrameLayout.refreshComplete();
                }
            }
        };
        doRequest(flowable, flowableSubscriber, EPType.SILENT);
    }

}
