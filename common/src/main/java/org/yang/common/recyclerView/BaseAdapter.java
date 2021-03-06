package org.yang.common.recyclerView;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Created by Gxy on 2017/6/20
 */

public interface BaseAdapter {

    RecyclerView.Adapter getAdapter();

    void addData(int index, List data);

    void addData(int index, Object object);

    Object getItem(int position);

    List getData();

    void setData(List data);

    void addData(Object object);

    void addData(List data);

    void notifyDataSetChanged();

    void remove(Object object);

    void notifyItemChanged(Object object);

}
