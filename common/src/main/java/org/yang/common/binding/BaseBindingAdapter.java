package org.yang.common.binding;

import android.content.res.AssetManager;
import android.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Gxy on 2017/5/5
 */

public class BaseBindingAdapter {


    /**
     * 绑定可选择控件,如:图片背景选择(解决属性不对应的问题)
     *
     * @param view
     * @param onLongClickListener
     */
    @BindingAdapter("onLongClickListener")
    public static void onLongClick(View view, View.OnLongClickListener onLongClickListener) {
        view.setOnLongClickListener(onLongClickListener);
    }

    /**
     * 绑定可选择控件,如:图片背景选择(解决属性不对应的问题)
     *
     * @param view
     * @param selected
     */
    @BindingAdapter("viewSelected")
    public static void setViewSelected(View view, boolean selected) {
        view.setSelected(selected);
    }

    /**
     * 绑定可选择控件,如:图片背景选择(解决属性不对应的问题)
     *
     * @param view
     * @param icon
     */
    @BindingAdapter("icon")
    public static void setIconFont(TextView view, String icon) {
        AssetManager assetManager = view.getContext().getAssets();
        Typeface typeface = Typeface.createFromAsset(assetManager, "iconfont.ttf");
        view.setTypeface(typeface);
        view.setText(icon);
    }

    /**
     * 时间格式化
     *
     * @param textView
     * @param needDateStr
     * @param currentPattern
     * @param needPattern
     */
    @BindingAdapter({"formatDate", "currentPattern", "needPattern"})
    public static void formatDate(TextView textView, String needDateStr, String currentPattern, String needPattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(currentPattern);
        Date date = null;
        try {
            date = simpleDateFormat.parse(needDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date != null) {
            textView.setText(String.format(needPattern, date));
        }

    }

}
