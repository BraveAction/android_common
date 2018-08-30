package org.yang.common.recyclerView;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gxy on 2017/5/5
 */
public class
SuperAdapter extends RecyclerView.Adapter<SuperViewHolder> implements BaseAdapter {
    protected
    @LayoutRes
    Integer[] layoutId;     //同一列表下的不同单元格
    protected int variableId;       //同一列表下的单元格使用同一别名标示
    protected List data = new ArrayList();
    protected IItemClickListener iItemClickListener;
//    private android.databinding.DataBindingComponent mDataBindingComponent;

    public SuperAdapter(int layoutResId, int variableId) {
        this(new Integer[]{layoutResId}, variableId, null);
    }

    public SuperAdapter(int layoutResId, int variableId, IItemClickListener iItemClickListener) {
        this(new Integer[]{layoutResId}, variableId, iItemClickListener);
    }

    public SuperAdapter(int layoutResId, int variableId, List data, IItemClickListener iItemClickListener) {
        this(new Integer[]{layoutResId}, variableId, iItemClickListener);
        this.data = data;
    }

//    public SuperAdapter(int layoutResId, int variableId, IItemClickListener iItemClickListener, android.databinding.DataBindingComponent dataBindingComponent) {
//        this(layoutResId, variableId, iItemClickListener);
//        this.mDataBindingComponent = dataBindingComponent;
//    }

    public SuperAdapter(Integer[] layoutResIds, int variableId, IItemClickListener iItemClickListener) {
        this.layoutId = layoutResIds;
        this.variableId = variableId;
        this.iItemClickListener = iItemClickListener;
    }
//
//    public android.databinding.DataBindingComponent getDataBindingComponent() {
//        return mDataBindingComponent;
//    }
//
//    public void setDataBindingComponent(android.databinding.DataBindingComponent mDataBindingComponent) {
//        this.mDataBindingComponent = mDataBindingComponent;
//    }

    @Override
    public SuperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        //viewType的类型码,按ItemView的布局文件所在的下标对应,且必须实现getItemView方法
        ViewDataBinding viewDataBinding;
//        if (mDataBindingComponent == null) {
        viewDataBinding = DataBindingUtil.inflate(layoutInflater, layoutId[viewType], parent, false);
//        } else {
//            viewDataBinding = DataBindingUtil.inflate(layoutInflater, layoutId[viewType], parent, false, mDataBindingComponent);
//        }

        if (viewDataBinding != null) {
            if (layoutId.length == 1) {     //只有一个布局无需传递viewType
                return getViewHolder(viewDataBinding, iItemClickListener);
            } else {
                return getViewHolder(viewDataBinding, viewType, iItemClickListener);
            }
        }
        return null;
    }

    protected SuperViewHolder getViewHolder(ViewDataBinding viewDataBinding, IItemClickListener iItemClickListener) {
        return new SuperViewHolder(viewDataBinding, iItemClickListener);
    }

    protected SuperViewHolder getViewHolder(ViewDataBinding viewDataBinding, int viewType, IItemClickListener iItemClickListener) {
        return new SuperViewHolder(viewDataBinding, viewType, iItemClickListener);
    }

    @Override
    public List getData() {
        return data;
    }

    @Override
    public void setData(List data) {
        this.data = data == null ? new ArrayList() : data;
        notifyDataSetChanged();
    }

    @Override
    public void addData(Object object) {
        this.data.add(object);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.Adapter getAdapter() {
        return this;
    }

    @Override
    public void addData(List data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void addData(int index, Object object) {
        this.data.add(index, object);
        notifyDataSetChanged();
    }

    @Override
    public void addData(int index, List data) {
        this.data.addAll(index, data);
//        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        if (this.data.size() > 0) {
            return this.data.get(position);
        } else {
            return null;
        }
    }

    @Override
    public void notifyItemChanged(Object object) {
        if (this.data.contains(object)) {
            notifyItemChanged(this.data.indexOf(object));
        }
    }

    public void remove(Object object) {
        if (this.data.contains(object)) {
            notifyItemRemoved(this.data.indexOf(object));
            this.data.remove(object);
        }
    }

    public void remove(int position) {
        if (this.data.size() > 0) {
            notifyItemRemoved(position);
            this.data.remove(position);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(SuperViewHolder holder, int position) {
        holder.viewDataBinding.setVariable(variableId, data.get(position));
        holder.viewDataBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
