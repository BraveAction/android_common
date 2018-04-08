package org.yang.common.components;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import org.yang.common.R;

import java.util.ArrayList;

/**
 * 菜单控件头部，封装了下拉动画，动态生成头部按钮个数
 * created by Gxy on 2016/03/02
 */

public class ExpandTabView extends LinearLayout implements OnDismissListener {
    private final String TAG = getClass().getSimpleName();
    private final int SMALL = 0;
    private ToggleButton selectedButton;
    private ArrayList<String> mTextArray = new ArrayList<String>();
    private ArrayList<RelativeLayout> mViewArray = new ArrayList<RelativeLayout>();
    private ArrayList<ToggleButton> mToggleButton = new ArrayList<ToggleButton>();
    private Context mContext;
    private int displayWidth;
    private int displayHeight;
    private MyPopupWindow popupWindow;
    private int selectPosition;
    //    private ArrayList<View> mViewArray;
    private OnButtonClickListener mOnButtonClickListener;

    public ExpandTabView(Context context) {
        super(context);
        init(context);
    }

    public ExpandTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 根据选择的位置设置tabitem显示的值
     */
    public void setTitle(String valueText, int position) {
        if (position < mToggleButton.size()) {
            mToggleButton.get(position).setText(valueText);
        }
    }

    /**
     * 根据选择的位置获取tabitem显示的值
     */
    public String getTitle(int position) {
        if (position < mToggleButton.size() && mToggleButton.get(position).getText() != null) {
            return mToggleButton.get(position).getText().toString();
        }
        return "";
    }

    /**
     * 设置tabitem的个数和初始值
     */
    public void setValue(ArrayList<String> textArray, final ArrayList<View> viewArray) {
        if (mContext == null) {
            return;
        }
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        this.mViewArray = viewArray;
        mTextArray = textArray;
        for (int i = 0; i < viewArray.size(); i++) {
            final RelativeLayout r = new RelativeLayout(mContext);
            final ViewBaseAction viewBaseAction = (ViewBaseAction) viewArray.get(i);
            float matrixScale = viewBaseAction.getMatrixScale();
            int maxHeight = matrixScale != 0 ? (int) (displayHeight * matrixScale) : RelativeLayout.LayoutParams.WRAP_CONTENT;
            RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, maxHeight);
            rl.leftMargin = -10;
            rl.rightMargin = -10;
            r.addView(viewArray.get(i), rl);
            mViewArray.add(r);
            r.setTag(SMALL);
            ToggleButton tButton = (ToggleButton) inflater.inflate(R.layout.toggle_button, this, false);
            if (viewBaseAction.getType() == ViewBaseAction.NO_MENU) {
                tButton.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.none_choosebar_press1));
            }
            addView(tButton);

//            添加垂直边线
            View line = new View(mContext);
            line.setBackgroundResource(android.R.color.darker_gray);
            if (i < viewArray.size() - 1) {
                LayoutParams lp = new LayoutParams(1, LayoutParams.MATCH_PARENT);

//                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics());
//                lp.setMargins(0, margin, 0, margin);

                addView(line, lp);
            }

            mToggleButton.add(tButton);
            tButton.setTag(i);
            tButton.setText(mTextArray.get(i));

            //点击空白区域隐藏列表窗口
            r.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPressBack();
                }
            });

            r.setBackgroundColor(getResources().getColor(R.color.popup_main_background));
            tButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    ToggleButton tButton = (ToggleButton) view;

                    if (selectedButton != null && selectedButton != tButton) {
                        selectedButton.setChecked(false);
                    }

                    selectedButton = tButton;

                    int i1 = (Integer) selectedButton.getTag();
                    if (mOnButtonClickListener != null) {
                        if (selectPosition != i1) {
                            mOnButtonClickListener.onClick(i1);
                        }
                    }
                    selectPosition = i1;
                    startAnimation();
                    setSelectedBtnStyle();
                }
            });

        }
    }

    private void startAnimation() {
        if (popupWindow == null) {
            popupWindow = new MyPopupWindow(mViewArray.get(selectPosition), displayWidth, displayHeight);
            popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
            popupWindow.setFocusable(false);
            popupWindow.setOutsideTouchable(true);
        }

        if (selectedButton.isChecked()) {
            if (!popupWindow.isShowing()) {
                showPopup(selectPosition);
            } else {
                popupWindow.setOnDismissListener(this);
                popupWindow.dismiss();
                hideView();
            }
        } else {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
                hideView();
            }
        }
    }

    private void showPopup(int position) {
        View tView = mViewArray.get(selectPosition).getChildAt(0);
        if (tView instanceof ViewBaseAction) {
            ViewBaseAction f = (ViewBaseAction) tView;
            f.show();
        }
        if (popupWindow.getContentView() != mViewArray.get(position)) {
            popupWindow.setContentView(mViewArray.get(position));
        }
        if (((ViewBaseAction) tView).getType() != ViewBaseAction.NO_MENU) {
            popupWindow.showAsDropDown(mToggleButton.get(position));
        }
    }


    private void setSelectedBtnStyle() {
        for (int i = 0; i < mToggleButton.size(); i++) {

            ToggleButton toggleButton = mToggleButton.get(i);
            View tView = mViewArray.get(i).getChildAt(0);
            //设置选中/未选中的颜色
            if (toggleButton == selectedButton) {
                toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.none_choosebar_press_up));
                toggleButton.setTextColor(ContextCompat.getColor(mContext, android.R.color.black));
//            设置选中/未选中的图标
                if (tView instanceof ViewBaseAction) {
                    ViewBaseAction selectOptionView = ((ViewBaseAction) tView);
                    if (selectOptionView.getType() == ViewBaseAction.NO_MENU) {
                        int orderType = selectOptionView.getOrderType();
                        boolean orderASC = (orderType == ViewBaseAction.ASC);
                        selectOptionView.setOrderType(orderASC ? ViewBaseAction.DESC : ViewBaseAction.ASC);
                        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(mContext, orderASC ? R.drawable.none_choosebar_press_up : R.drawable.none_choosebar_press_down));
                    } else {
                        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.expand_tab_selector));
                    }
                }
            } else {
                if (tView instanceof ViewBaseAction) {
                    ViewBaseAction selectOptionView = ((ViewBaseAction) tView);
                    if (selectOptionView.getType() == ViewBaseAction.NO_MENU) {
                        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.none_choosebar_press1));
                        toggleButton.setTextColor(ContextCompat.getColor(mContext, android.R.color.darker_gray));
                    } else {
                        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.expand_tab_selector));
                    }
                }
            }
        }
    }

    /**
     * 如果菜单成展开状态，则让菜单收回去
     */

    public boolean onPressBack() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            hideView();
            if (selectedButton != null) {
                selectedButton.setChecked(false);
            }
            return true;
        } else {
            return false;
        }
    }

    private void hideView() {
        View tView = mViewArray.get(selectPosition).getChildAt(0);
        if (tView instanceof ViewBaseAction) {
            ViewBaseAction f = (ViewBaseAction) tView;
            f.hide();
        }
    }

    private void init(Context context) {
        mContext = context;
        displayWidth = getResources().getDisplayMetrics().widthPixels;
        displayHeight = getResources().getDisplayMetrics().heightPixels;
        setOrientation(LinearLayout.HORIZONTAL);
    }

    @Override
    public void onDismiss() {
        showPopup(selectPosition);
        popupWindow.setOnDismissListener(null);
    }

    /**
     * 设置tabitem的点击监听事件
     */
    public void setOnButtonClickListener(OnButtonClickListener l) {
        mOnButtonClickListener = l;
    }

    /**
     * 自定义tabitem点击回调接口
     */
    public interface OnButtonClickListener {
        void onClick(int selectPosition);
    }

}
