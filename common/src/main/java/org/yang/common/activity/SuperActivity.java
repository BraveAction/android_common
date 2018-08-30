package org.yang.common.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.yang.common.BR;
import org.yang.common.R;
import org.yang.common.annotation.ErrorViewOnClickCallBack;
import org.yang.common.base.BaseResponse;
import org.yang.common.base.BaseWindow;
import org.yang.common.base.NetRequestHelper;
import org.yang.common.components.CustomProgressDialog;
import org.yang.common.net.BaseConsumer;
import org.yang.common.net.BaseConsumerFactory;
import org.yang.common.net.EPType;
import org.yang.common.net.RetrofitClient;
import org.yang.common.net.RxSchedules;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;

/**
 * Created by Gxy on 2017/4/1
 */
public abstract class SuperActivity<VB extends ViewDataBinding, S, RES extends BaseResponse> extends AppCompatActivity implements BaseWindow, View.OnClickListener, NetRequestHelper {
    protected S mApiServices;
    protected VB mBinding;
    private TextView mTitleTextView;
    private RetrofitClient mRetrofitClient;
    //    private ProgressDialog mProgressDialog;
    private CustomProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int layoutId = getLayoutId();
        if (layoutId == 0) {
            throw new IllegalArgumentException("View is not a binding layout");
        }

        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
        mBinding.setVariable(BR.onClickListener, this);
        onInit();
    }

    /**
     * 获取界面布局id
     *
     * @return
     */
    protected abstract
    @LayoutRes
    int getLayoutId();

    /*在子Activity中调用
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainActivity);
        super.onInit();
    }*/
    public void onInit() {
        init();
    }

    /**
     * 初始化数据
     */
    public void init() {
        initServices();
        initCommonViews();
        inflateViews();
    }

    /**
     * 初始化网络请求接口
     */
    protected void initServices() {
        mRetrofitClient = RetrofitClient.getInstance();
        if (mRetrofitClient != null) {
            mApiServices = (S) mRetrofitClient.getServices();
        }
    }

    /**
     * 初始化通用视图
     */
    @Override
    public void initCommonViews() {
        initToolbar(R.id.toolbar);
        initProgressDialog();
    }

    /**
     * 获取网络请求接口服务
     *
     * @return
     */
    public S getApiServices() {
        return mApiServices;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mComposite != null) {
            mComposite.clear();
        }
    }

    @Override
    public void add(Disposable disposable) {
        if (mComposite != null) {
            mComposite.add(disposable);
        }
    }

    //--------------------------------------------------------//
    //----------------------进度条---------------------------//
    //--------------------------------------------------------//

    @Override
    public void hideProgressDialog() {
        Dialog progressDialog = getProgressDialog();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 获取统一网络请求进度条
     *
     * @return
     */
    public CustomProgressDialog getProgressDialog() {
        return mProgressDialog;
    }

    /**
     * 初始化网络请求进度条
     */
    protected void initProgressDialog() {
        mProgressDialog = new CustomProgressDialog(this);
        setProgressDialogMsg(R.string.progressDialogMsg);
        mProgressDialog.setCancelable(false);
    }

    /**
     * 显示网络请求进度条
     */
    @Override
    public void showProgressDialog() {
        this.runOnUiThread(() -> mProgressDialog.show());
    }

    /**
     * 设置网络请求进度文本
     *
     * @param msgRes
     */
    protected void setProgressDialogMsg(@StringRes int msgRes) {
        setProgressDialogMsg(getString(msgRes));
    }

    /**
     * 设置网络请求进度文本
     *
     * @param message
     */
    protected void setProgressDialogMsg(CharSequence message) {
        if (mProgressDialog != null) {
            mProgressDialog.setMessage(message);
        }
    }


    //--------------------------------------------------------//
    //----------------------toolbar---------------------------//
    //--------------------------------------------------------//

    /**
     * 初始化toolbar
     */
    protected void initToolbar(@IdRes int toolbarId) {
        Toolbar toolbar = findViewById(toolbarId);
        if (toolbar != null) {
//        设置标题
            toolbar.setTitle(getTitle());
//        设置子标题
//        toolbar.setSubtitle(getTitle());
//        设置导航按键
//        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.navg));
//        设置标题文本颜色
//            toolbar.setTitleTextColor(0xFFC000);
//        设置子标题文本颜
// 色
//            toolbar.setSubtitleTextColor(Color.argb(255, 20, 20, 255));
//        设置ActionBar
            setSupportActionBar(toolbar);

//        ActionBar不要显示标题,自定义的标题会居中
            getSupportActionBar().setDisplayShowTitleEnabled(false);
//        设置返回按键图片
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back);
//        显示返回按键
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        设置标题
            mTitleTextView = toolbar.findViewById(R.id.toolbarTitle);
            if (mTitleTextView != null) {
                CharSequence titleName = getTitle();
                if (!TextUtils.isEmpty(titleName)) {
                    mTitleTextView.setText(titleName);
                } else {
                    throw new RuntimeException("请在AndroidManifest.xml中为Activity设置label");
                }
            }
        }
    }

    public void setToolbarTitle(String title) {
        if (mTitleTextView != null) {
            mTitleTextView.setText(title);
        }
    }

    /**
     * 执行toolbar点击返回事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }


    //--------------------------------------------------------//
    //----------------------toast提示---------------------------//
    //--------------------------------------------------------//

    @Override
    public void showMessage(String errorMsg) {
        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showMessage(@StringRes int errorMsgRes) {
        showMessage(getString(errorMsgRes));
    }


    //--------------------------------------------------------//
    //----------------------异常处理---------------------------//
    //--------------------------------------------------------//

    @Override
    public void onHandleException(EPType EPType, int errorMsg) {

        switch (EPType) {
            case PAGE:
                View errorLayout = findViewById(R.id.errorLayout);
                if (errorLayout != null) {
                    errorLayout.setVisibility(View.VISIBLE);
                    TextView errorMsgHint = errorLayout.findViewById(R.id.errorMsg);
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

    /**
     * 点击指定的异常视图
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.errorLayout) {
            executeErrorViewOnClickEvent();
        }
    }

    /**
     * 执行activity中ErrorViewOnClickCallBack注解的方法
     */
    @SuppressLint("LongLogTag")
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
                View errorLayout = findViewById(R.id.errorLayout);
                if (errorLayout != null) {
//                    errorLayout.setVisibility(View.GONE);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            Log.w("SuperActivity#executeErrorViewOnClickEvent",
                    "异常视图界面点击无效,在" + this.getClass().getSimpleName() + "类中没有找到ErrorViewOnClickCallBack注解的方法");
        }
    }


    //--------------------------------------------------------//
    //----------------------网络请求---------------------------//
    //--------------------------------------------------------//

    public void doRequest(Flowable flowable, FlowableSubscriber subscriber, EPType baseError) {
        doRequest(flowable, subscriber, BaseConsumerFactory.getInstance(this, baseError));
    }

    @Override
    public void doRequest(Flowable flowable, FlowableSubscriber subscriber, BaseConsumerFactory baseConsumerFactory) {
        mComposite.add((Disposable) (flowable.compose(RxSchedules.mainThread()).
                doOnSubscribe(baseConsumerFactory.create(BaseConsumer.START)).
                doOnNext(baseConsumerFactory.create(BaseConsumer.NEXT)).
                doOnComplete(() -> this.hideProgressDialog()).
                doOnError(baseConsumerFactory.create(BaseConsumer.ERROR)))
                .subscribeWith(subscriber));
    }

}
