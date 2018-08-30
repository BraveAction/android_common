package org.yang.common.components;

import android.animation.ObjectAnimator;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by gxy on 2018/7/11
 * 描述:
 */
public abstract class TranslationTransformer implements ViewPager.PageTransformer {
    private static final long SEL_DURATION = 200;
    private static final long HIDE_DURATION = 150;
    private static final float HIDE_DOWN_VALUES = 25f;
    private static final float SEL_UP_VALUES = -25f;
    private ViewPager viewPager;


    public TranslationTransformer(ViewPager pager) {
        this.viewPager = pager;
//        elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
//                10, context.getResources().getDisplayMetrics());
    }

    @Override
    public void transformPage(View page, float position) {
        ObjectAnimator valueAnimator = new ObjectAnimator();
        valueAnimator.setPropertyName("translationY");
        valueAnimator.setTarget(page);
        valueAnimator.setFloatValues(HIDE_DOWN_VALUES);
        valueAnimator.setDuration(HIDE_DURATION);
        if (position < -1 || position > 1) {
            changPageState(page, false);
//            valueAnimator.start();
        } else if (position <= 1) { // [-1,1]
            if (position < 0) {
                changPageState(page, false);
//                valueAnimator.start();
            } else {
                if (position == 0) {
                    if (viewPager.indexOfChild(page) == 0) {
                        valueAnimator.setFloatValues(SEL_UP_VALUES);
                        valueAnimator.setDuration(SEL_DURATION);
                        changPageState(page, true);
//                        valueAnimator.start();
                    } else {
                        changPageState(page, false);
//                        valueAnimator.start();
                    }
                } else {
                    valueAnimator.setFloatValues(SEL_UP_VALUES);
                    valueAnimator.setDuration(SEL_DURATION);
                    changPageState(page, true);
//                    valueAnimator.start();
                }
            }

        }
        valueAnimator.start();
    }

    /**
     * 设置页面透明度
     *
     * @param page
     * @param isSelected
     */
    protected abstract void changPageState(View page, boolean isSelected);

}

