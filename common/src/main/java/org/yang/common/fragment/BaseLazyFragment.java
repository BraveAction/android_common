package org.yang.common.fragment;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.yang.common.BR;
import org.yang.common.R;
import org.yang.common.activity.SuperActivity;
import org.yang.common.annotation.ErrorViewOnClickCallBack;
import org.yang.common.base.BaseWindow;
import org.yang.common.base.NetRequestHelper;
import org.yang.common.net.BaseConsumerFactory;
import org.yang.common.net.EPType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;

/**
 * Created by Gxy on 2017/2/4
 */
public abstract class BaseLazyFragment extends Fragment implements BaseWindow, View.OnClickListener, NetRequestHelper {
    private final int MEMORY_CACHE_MAXSIZE = 5 * 1024 * 1024;
    protected LruCache<Object, Object> mMemoryCache;
    protected boolean isViewInitiated;
    protected boolean isVisibleToUser;
    protected boolean isDataInitiated;

    protected ViewDataBinding mBinding;
    protected View mRootLayout;
    protected SuperActivity mContext;
    protected Toolbar mToolbar;


    public boolean isDataInitiated() {
        return isDataInitiated;
    }

    public void setDataInitiated(boolean dataInitiated) {
        isDataInitiated = dataInitiated;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SuperActivity) {
            mContext = (SuperActivity) context;
        } else {
            //Activity请继承com.pharmacy.common.BaseActivity
            throw new RuntimeException("OMG!代码撸的有问题!!!");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        init();
    }

    /**
     * 初始化数据
     */
    protected void init() {
        mMemoryCache = new LruCache<>(MEMORY_CACHE_MAXSIZE);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRootLayout = getView();
        inflateViews();
        initCommonViews();
        isViewInitiated = true;
        prepareFetchData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootLayout != null) {
            return mRootLayout;
        }
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mBinding.setVariable(BR.onClickListener, this);
        mRootLayout = mBinding.getRoot();
        return mRootLayout;
    }

    protected abstract
    @LayoutRes
    int getLayoutId();

    @Override
    public void onResume() {
        super.onResume();
//        onRestoreInstanceState();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.errorLayout) {
            executeErrorViewOnClickEvent();
            View errorLayout = mRootLayout.findViewById(R.id.errorLayout);
            if (errorLayout != null) {
                errorLayout.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 执行activity中ErrorViewOnClickCallBack注解的方法
     */
    private void executeErrorViewOnClickEvent() {
        Method executeMethod = null;
        for (Method method : this.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(ErrorViewOnClickCallBack.class)) {
                executeMethod = method;
            }
        }

        if (executeMethod != null) {
            try {
                if (!executeMethod.isAccessible()) executeMethod.setAccessible(true);
                executeMethod.invoke(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRootLayout != null) {
            ((ViewGroup) mRootLayout.getParent()).removeView(mRootLayout);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        prepareFetchData();
    }

    @Override
    public void initCommonViews() {
        initToolbar(R.id.toolbar);
//        initProgressDialog();
    }

    /**
     * 如果toolbar不是默认id,不会显示(需要重写些方法传递toobarId)
     * 初始化toolbar
     */
    protected void initToolbar(@IdRes final int toolbarId) {
        mToolbar = (Toolbar) mRootLayout.findViewById(toolbarId);
        if (mToolbar != null) {
//        设置标题
            mToolbar.setTitle(mContext.getTitle());
//        设置子标题
//        mToolbar.setSubtitle(getTitle());
//        设置导航按键
//        mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.navg));
//        设置标题文本颜色
//            mToolbar.setTitleTextColor(0xFFC000);
//        设置子标题文本颜色
//            mToolbar.setSubtitleTextColor(Color.argb(255, 20, 20, 255));
//        设置ActionBar
            mContext.setSupportActionBar(mToolbar);

//        ActionBar不要显示标题,自定义的标题会居中
            mContext.getSupportActionBar().setDisplayShowTitleEnabled(false);
//        设置返回按键图片
//            mContext.getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_back);
//        显示返回按键
            mContext.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //去除导航按钮的边距
            mToolbar.setContentInsetStartWithNavigation(0);
//        设置标题
            TextView title = ((TextView) mToolbar.findViewById(R.id.toolbarTitle));
            if (title != null) {
                CharSequence titleName = mContext.getTitle();
                if (!TextUtils.isEmpty(titleName)) {
                    title.setText(titleName);
                } else {
                    throw new RuntimeException("请在AndroidManifest.xml中为Activity设置label");
                }
            }
        }
    }

    protected void showNoMoreMsg(View view) {
        Snackbar snackbar = Snackbar.make(view, R.string.noMore, Snackbar.LENGTH_SHORT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        //黑底白字
        ((TextView) snackbarLayout.findViewById(R.id.snackbar_text)).setTextColor(ContextCompat.getColor(mContext, R.color.black));
        snackbarLayout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.activityBgColor));
        snackbar.show();
    }

    public boolean prepareFetchData() {
        return prepareFetchData(false);
    }

    public boolean prepareFetchData(boolean forceUpdate) {
        if (isVisibleToUser && isViewInitiated && (!isDataInitiated || forceUpdate)) {
            fetchData();
            isDataInitiated = true;
            return true;
        }
        return false;
    }

//    protected abstract void onRestoreInstanceState();
//
//    /**
//     * 保存界面状态
//     */
//    protected void onSaveInstanceState(Object object) {
//        if (mMemoryCache != null) {
//            mMemoryCache.put(getTag(), object);
//        }
//    }
//
//    /**
//     * 保存界面状态
//     */
//    protected void onSaveInstanceState(Object key, Object value) {
//        if (mMemoryCache != null) {
//            mMemoryCache.put(key, value);
//        }
//    }
//
//
//    protected Object getMemoryCache(Object key) {
//        if (mMemoryCache != null) {
//            if (mMemoryCache.putCount() > 0) {
//                return mMemoryCache.get(key);
//            }
//        }
//        return null;
//    }

    public abstract void fetchData();

    @Override
    public void onHandleException(EPType EPType, int errorMsg) {
        switch (EPType) {
            case PAGE:
                View errorLayout = mRootLayout.findViewById(org.yang.common.R.id.errorLayout);
                if (errorLayout != null) {
                    errorLayout.setVisibility(View.VISIBLE);
                    TextView errorMsgHint = (TextView) errorLayout.findViewById(org.yang.common.R.id.errorMsg);
                    if (errorMsgHint != null) {
                        errorMsgHint.setText(errorMsg);
                    }
                }
                break;
            case SILENT:
                break;
            case DIALOG:
            case TOAST:
            default:
                showMessage(errorMsg);
        }
    }

    @Override
    public void hideProgressDialog() {
        mContext.hideProgressDialog();
    }

    @Override
    public void showProgressDialog() {
        Dialog progressDialog = mContext.getProgressDialog();
        if (progressDialog != null) {
            progressDialog.show();
        }
    }

    @Override
    public void doRequest(Flowable flowable, FlowableSubscriber subscriber, BaseConsumerFactory baseConsumerFactory) {
        if (mContext != null) {
            mContext.doRequest(flowable, subscriber, baseConsumerFactory);
        }
    }

    public void doRequest(Flowable flowable, FlowableSubscriber subscriber, EPType baseError) {
        if (mContext != null) {
            mContext.doRequest(flowable, subscriber, BaseConsumerFactory.getInstance(this, baseError));
        }
    }

    @Override
    public void showMessage(String errorMsg) {
        Toast.makeText(getActivity().getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(@StringRes int errorMsgRes) {
        showMessage(getString(errorMsgRes));
    }

}
