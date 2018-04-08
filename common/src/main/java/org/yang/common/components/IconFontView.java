package org.yang.common.components;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by Gxy on 2017/3/29
 */

public class IconFontView extends android.support.v7.widget.AppCompatTextView {
    public static final String ICONFONT_PHARMACY_TTF = "iconfont_pharmacy.ttf";
    private Typeface typeface;

    public IconFontView(Context context) {
        super(context);
    }

    public IconFontView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IconFontView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        AssetManager assetManager = context.getAssets();
        typeface = Typeface.createFromAsset(assetManager, ICONFONT_PHARMACY_TTF);
        setTypeface(typeface);
    }

}
