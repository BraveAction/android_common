package org.yang.common.components;

import android.content.Context;
import android.graphics.PointF;
import android.widget.ImageView;

/**
 * Created by Gxy on 2017/5/16
 */

public class MoveImageView extends ImageView {

    public MoveImageView(Context context) {
        super(context);
    }

    public void setMPointF(PointF pointF) {
        setX(pointF.x);
        setY(pointF.y);
    }
}
