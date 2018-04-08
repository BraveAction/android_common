package org.yang.common.recyclerView;

import android.view.View;

/**
 * RecyclerView.Adapter的点击事件接口
 * Created by gxy on 2015/10/17.
 */
public interface IItemClickListener {

    /**
     * 单击事件
     *
     * @param view     控件
     * @param position 下标
     */
    void onItemClick(View view, int position);


    /**
     * 长按事件
     *
     * @param view     控件
     * @param position 下标
     */
    void onItemLongClick(View view, int position);

}
