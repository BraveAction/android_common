package org.yang.common.components;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.yang.common.R;

/**
 * Created by Gxy on 2015-07-23.
 * <p/>
 * bug:当调用show方法时,状态栏颜色会闪烁,待修改
 */
public class CustomProgressDialog extends Dialog {

    private ImageView mProgressImg;
    private TextView mMessage;
    private Animation mProgressRotate;

    public CustomProgressDialog(Context context) {
        super(context, R.style.progress_style);
        LayoutInflater mInflater = LayoutInflater.from(context);
        LinearLayout progressLoadingLayout = (LinearLayout) mInflater.inflate(R.layout.my_dialog_loading_progress, null);
        mProgressImg = (ImageView) progressLoadingLayout.findViewById(R.id.id_fa_spinner);
        mMessage = (TextView) progressLoadingLayout.findViewById(R.id.id_text_msg);
        mProgressRotate = AnimationUtils.loadAnimation(context, R.anim.loading_rotate);
        int colorTint = ContextCompat.getColor(context, R.color.colorAccent);
        if (mProgressImg != null) {
            Drawable tintDrawable = getTintDrawable(mProgressImg.getDrawable(), colorTint, false);
            if (tintDrawable != null) {
                mProgressImg.setImageDrawable(tintDrawable);
            }
        }

        this.setCancelable(false);  //获取焦点,返回键无效
        this.addContentView(progressLoadingLayout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        this.setContentView(progressLoadingLayout);
    }


    /**
     * Return a tint drawable
     *
     * @param drawable
     * @param color
     * @param forceTint
     * @return
     */
    public static Drawable getTintDrawable(Drawable drawable, @ColorInt int color, boolean forceTint) {
        if (forceTint) {
            drawable.clearColorFilter();
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            drawable.invalidateSelf();
            return drawable;
        }
        Drawable wrapDrawable = DrawableCompat.wrap(drawable).mutate();
        DrawableCompat.setTint(wrapDrawable, color);
        return wrapDrawable;
    }


    @Override
    public void dismiss() {
        super.dismiss();
        mProgressImg.clearAnimation();
    }

    @Override
    public void show() {
        super.show();
        mProgressRotate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mProgressImg.setAnimation(mProgressRotate);
        mProgressRotate.start();
//        super.show();
    }

    public void setMessage(CharSequence message) {
        if (mMessage != null) {
            mMessage.setText(message);
        }
    }

}
