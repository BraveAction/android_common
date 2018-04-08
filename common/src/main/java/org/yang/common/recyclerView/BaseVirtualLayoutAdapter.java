package org.yang.common.recyclerView;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现了多布局列表下,查找对应数据集下标的方法
 * Created by Gxy on 2017/4/25
 */
public abstract class BaseVirtualLayoutAdapter extends VirtualLayoutAdapter<SuperViewHolder> implements BaseAdapter {

    public List data = new ArrayList();
    protected
    @LayoutRes
    Integer[] layoutId;
    protected int variableId;
    protected IItemClickListener iItemClickListener;

    public BaseVirtualLayoutAdapter(@NonNull VirtualLayoutManager layoutManager, int layoutResId, int variableId, IItemClickListener iItemClickListener) {
        super(layoutManager);
        this.layoutId = new Integer[]{layoutResId};
        this.variableId = variableId;
        this.iItemClickListener = iItemClickListener;
    }

    public BaseVirtualLayoutAdapter(@NonNull VirtualLayoutManager layoutManager, Integer[] layoutResIds, int variableId, IItemClickListener iItemClickListener) {
        super(layoutManager);
        this.layoutId = layoutResIds;
        this.variableId = variableId;
        this.iItemClickListener = iItemClickListener;
    }

    public BaseVirtualLayoutAdapter(@NonNull VirtualLayoutManager layoutManager) {
        super(layoutManager);
    }


    /**
     * 通过布局个数来区分单元格类型
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        LayoutHelper currentLayoutHelper = mLayoutManager.findLayoutHelperByPosition(position);
        List<LayoutHelper> layoutHelpers = getLayoutHelpers();
        for (int i = 0; i < layoutHelpers.size(); i++) {
            int itemType = i;
            LayoutHelper layoutHelper = layoutHelpers.get(i);
            if (currentLayoutHelper == layoutHelper) {
                return itemType;
            }
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (this.data.size() > 0) {
            return this.data.get(position);
        } else {
            return null;
        }
    }

    /**
     * @param position
     * @return
     */
    public int getRealPosition(int position) {

        LayoutHelper visibleLayoutHelper = mLayoutManager.findLayoutHelperByPosition(position);
        List<LayoutHelper> helpers = getLayoutHelpers();
        int aboveItemCount = 0;
        for (int i = 0; i < helpers.size(); i++) {
            LayoutHelper layoutHelper = helpers.get(i);
            if (layoutHelper == visibleLayoutHelper) {
                break;
            }
            aboveItemCount += layoutHelper.getItemCount();
        }
        return position - aboveItemCount;
    }

    @Override
    public int getItemCount() {
        List<LayoutHelper> helpers = getLayoutHelpers();
        if (helpers == null) {
            return 0;
        }
        int count = 0;
        for (int i = 0, size = helpers.size(); i < size; i++) {
            count += helpers.get(i).getItemCount();
        }
        return count;
    }

    @Override
    public SuperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        //viewType的类型码,按ItemView的布局文件所在的下标对应,且必须实现getItemView方法
        ViewDataBinding viewDataBinding = DataBindingUtil.inflate(layoutInflater, layoutId[viewType], parent, false);
        if (viewDataBinding != null) {
            if (layoutId.length == 1) {     //只有一个布局无需传递viewType
                return getViewHolder(viewDataBinding, iItemClickListener);
            } else {
                return getViewHolder(viewDataBinding, viewType, iItemClickListener);
            }
        }
        return null;
    }

    @Override
    public List getData() {
        return data;
    }

    @Override
    public void setData(List data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public void addData(List data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void remove(Object object) {
        if (this.data.contains(object)) {
            notifyItemRemoved(this.data.indexOf(object));
            this.data.remove(object);
        }
    }

    @Override
    public void notifyItemChanged(Object object) {
        if (this.data.contains(object)) {
            notifyItemChanged(this.data.indexOf(object));
        }
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return this;
    }

    protected SuperViewHolder getViewHolder(ViewDataBinding viewDataBinding, IItemClickListener iItemClickListener) {
        return new SuperViewHolder(viewDataBinding, iItemClickListener);
    }

    protected SuperViewHolder getViewHolder(ViewDataBinding viewDataBinding, int viewType, IItemClickListener iItemClickListener) {
        return new SuperViewHolder(viewDataBinding, viewType, iItemClickListener);
    }

    @Override
    public void onBindViewHolder(SuperViewHolder holder, int position) {
        holder.viewDataBinding.setVariable(variableId, data.get(position));
        holder.viewDataBinding.executePendingBindings();
    }
}
