package org.yang.common.components;

public interface ViewBaseAction {

    int ASC = 1;        //orderType
    int DESC = 2;

    int NO_MENU = 1;
    int HAS_MENU = 2;

    int getOrderType();

    void setOrderType(int mOrderType);

    int getType();

    float getMatrixScale();

    /**
     * 菜单隐藏操作
     */
    void hide();

    /**
     * 菜单显示操作
     */
    void show();

}
