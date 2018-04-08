package org.yang.common.activity;

import android.databinding.ViewDataBinding;
import android.os.IInterface;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;

import org.reactivestreams.Subscription;
import org.yang.common.R;
import org.yang.common.activity.SuperActivity;
import org.yang.common.base.BaseResponse;
import org.yang.common.components.OnRecyclerViewLoadListener;
import org.yang.common.net.EPType;
import org.yang.common.recyclerView.BaseAdapter;
import org.yang.common.recyclerView.RecyclerViewEmptySupport;
import org.yang.common.recyclerView.RecyclerViewUtils;
import org.yang.common.recyclerView.SuperAdapter;

import java.util.List;

import in.srain.cube.views.ptr.PtrFrameLayout;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.annotations.NonNull;

/**
 * 单个列表(RecyclerView)界面
 * 1:实现了下拉刷新,上拉加载功能
 * 2:没有适配器数据时显示异常界面
 * Created by Gxy on 2017/5/24
 */

public abstract class BaseRecyclerViewActivity<VB extends  ViewDataBinding,S, RESP extends BaseResponse> extends SuperActivity<VB,S,RESP> implements OnRecyclerViewLoadListener {
    protected PtrFrameLayout mPtrFrameLayout;
    protected int mCurrentPage = 1;     //分页结果集使用
    protected BaseAdapter mAdapter;
    protected RecyclerViewEmptySupport mRecyclerView;

    protected
    @LayoutRes
    int getLayoutId() {
        return R.layout.activity_ptrl_recyclerview;
    }

    @Override
    public void inflateViews() {
        mRecyclerView = (RecyclerViewEmptySupport) findViewById(R.id.recyclerView);
        RecyclerViewUtils.setBaseProperties(mRecyclerView);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setEmptyView(findViewById(R.id.errorLayout));
        mRecyclerView.setLayoutManager(getLayoutManager());

        mAdapter = getAdapter();
        mRecyclerView.setAdapter(mAdapter.getAdapter());

        mPtrFrameLayout = (PtrFrameLayout) findViewById(R.id.refreshLayout);
        if (mPtrFrameLayout != null) {
            RecyclerViewUtils.setRecyclerViewPtrl(mRecyclerView, mPtrFrameLayout, this);
            mPtrFrameLayout.autoRefresh();
        } else {
            onRefresh(null);
        }
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
    protected abstract Flowable getRequestParameter(OnRecyclerViewLoadListener.PullOperation operation);


    @Override
    public void onRefresh(PtrFrameLayout frame) {
        mCurrentPage = 1;
        Flowable flowable = getRequestParameter(OnRecyclerViewLoadListener.PullOperation.REFRESH);
        if (flowable == null) {
            return;
        }
        FlowableSubscriber flowableSubscriber = new FlowableSubscriber<RESP>() {
            @Override
            public void onSubscribe(@NonNull Subscription s) {
            }

            @Override
            public void onNext(RESP sendGoodOrder) {
                List list = packageData(sendGoodOrder);
                if (list != null) {
                    mAdapter.setData(list);
                }
            }


            /**
             * 异常说明:
             * ClassCastException:数据对象需要在接口中指定具体的类型
             */
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
        Flowable flowable = getRequestParameter(OnRecyclerViewLoadListener.PullOperation.LOADMORE);
        if (flowable == null) {
            return;
        }
        FlowableSubscriber flowableSubscriber = new FlowableSubscriber<RESP>() {
            @Override
            public void onSubscribe(@NonNull Subscription s) {

            }

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
