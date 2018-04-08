package org.yang.common.recyclerView;

import com.alibaba.android.vlayout.LayoutManagerHelper;
import com.alibaba.android.vlayout.layout.FixLayoutHelper;

/**
 * 通过指定列表滚动的下标来显示视图
 * Created by Gxy on 2017/4/26
 */
public class ScrollFixLayoutHelper extends FixLayoutHelper {

    public static final int SHOW_ALWAYS = 0;
    public static final int SHOW_ON_ENTER = 1;
    public static final int SHOW_ON_LEAVE = 2;
    public static final int SHOW_ON_POSITION = 3;
    private static final String TAG = "ScrollFixLayoutHelper";
    private int position = 0;
    private int mShowType = SHOW_ALWAYS;


    public ScrollFixLayoutHelper(int x, int y) {
        this(TOP_LEFT, x, y);
    }

    public ScrollFixLayoutHelper(int alignType, int x, int y) {
        super(alignType, x, y);
    }

    public int getShowType() {
        return mShowType;
    }

    public void setShowType(int showType) {
        this.mShowType = showType;
    }

    @Override
    protected boolean shouldBeDraw(LayoutManagerHelper helper, int startPosition, int endPosition, int scrolled) {
        switch (mShowType) {
            case SHOW_ON_POSITION:
                return endPosition >= position;
            case SHOW_ON_ENTER:
                // when previous item is entering
                return endPosition >= getRange().getLower() - 1;
            case SHOW_ON_LEAVE:
                // show on leave from top
                // when next item is the first one in screen
                return startPosition >= getRange().getLower() + 1;
            case SHOW_ALWAYS:
            default:
                // default is always
                return true;
        }

    }

    public int getPosition() {
        return position;
    }

    public void setShowPosition(int position) {
        this.position = position + 1;
    }
}