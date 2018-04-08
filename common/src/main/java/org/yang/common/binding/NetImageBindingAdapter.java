package org.yang.common.binding;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;

/**
 * Created by Gxy on 2017/6/7
 */

public class NetImageBindingAdapter {


    /**
     * 使用绑定框架,(Glide)加载图片
     *
     * @param imageView
     * @param url
     * @param placeholderDrawable
     */
    @BindingAdapter(value = {"netImageUrl", "placeholderResId"}, requireAll = false)
    public static void setNetImageUrl(ImageView imageView, String url, Drawable placeholderDrawable) {
        DrawableRequestBuilder drawableRequestBuilder = Glide.with(imageView.getContext()).load(url).crossFade()
                .fitCenter();
        if (placeholderDrawable != null) {
            drawableRequestBuilder.placeholder(placeholderDrawable);
        }
        drawableRequestBuilder.into(imageView);
    }

}
