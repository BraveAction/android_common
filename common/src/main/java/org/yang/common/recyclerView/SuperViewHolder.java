package org.yang.common.recyclerView;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * Created by Gxy on 2017/5/5
 */

public class SuperViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    public ViewDataBinding viewDataBinding;
    protected IItemClickListener iItemClickListener;


    protected SuperViewHolder(ViewDataBinding viewDataBinding) {
        super(viewDataBinding.getRoot());
        this.viewDataBinding = viewDataBinding;
        //viewHolder变量名是通用的,也可在自定义的ViewHolder中基于item_xxx.xml修改
        viewDataBinding.setVariable(org.yang.common.BR.viewHolder, this);
        viewDataBinding.setVariable(org.yang.common.BR.viewHolder2, this);

    }

    public SuperViewHolder(ViewDataBinding viewDataBinding, IItemClickListener iItemClickListener) {
        this(viewDataBinding);
        this.iItemClickListener = iItemClickListener;
    }


    public SuperViewHolder(ViewDataBinding viewDataBinding, int viewType, IItemClickListener iItemClickListener) {
        this(viewDataBinding, iItemClickListener);
    }

    @Override
    public void onClick(View v) {
        if (iItemClickListener != null) {
            iItemClickListener.onItemClick(v, getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (iItemClickListener != null) {
            iItemClickListener.onItemLongClick(v, getAdapterPosition());
            return true;
        }
        return false;
    }
}
