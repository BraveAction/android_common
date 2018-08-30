package org.yang.common.utils;

import org.greenrobot.greendao.AbstractDaoSession;

/**
 * Created by Gxy on 2017/6/16
 */

public final class DaoUtil {
    private static AbstractDaoSession mDaoSession;

    public static void newInstance(AbstractDaoSession daoSession) {
        mDaoSession = daoSession;
    }

    public static AbstractDaoSession getDaoSession() {
        return mDaoSession;
    }

}
