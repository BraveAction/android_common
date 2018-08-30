package org.yang.common.utils;


import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.QueryBuilder;
import org.yang.common.BuildConfig;

import java.util.List;

/**
 * Created by Gxy on 2017/6/16
 */

public class UserInfoUtil {

    /**
     * 开户日志
     *
     * @param queryBuilder
     */
    public static void showLog(QueryBuilder queryBuilder) {
        if (BuildConfig.DEBUG) {
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
        }
    }

    public static void deleteUserLoginInfo(Object loginInfo) {
        if (loginInfo != null) {
            AbstractDao dao = DaoUtil.getDaoSession().getDao(loginInfo.getClass());
            dao.delete(loginInfo);
        }
    }


    /**
     * 通过用户id获取用户信息
     *
     * @param clazz
     * @param key   主键
     * @return
     */
    public static <T> T getUserInfo(Class<T> clazz, Object key) {
        AbstractDao dao = DaoUtil.getDaoSession().getDao(clazz);

        QueryBuilder queryBuilder = dao.queryBuilder();
        showLog(queryBuilder);

        //在数据库中查找是否有此数据项
        try {
            return (T) queryBuilder.where(dao.getPkProperty().eq(key)).unique();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 通过用户id获取用户信息
     *
     * @param clazz
     * @return
     */
    public static <T> T getUser(Class<T> clazz) {
        AbstractDao dao = DaoUtil.getDaoSession().getDao(clazz);

        QueryBuilder queryBuilder = dao.queryBuilder();
        showLog(queryBuilder);

        //在数据库中查找是否有此数据项
        try {
            List<T> users = queryBuilder.list();
            if (users != null && users.size() > 0) {
                return users.get(0);        //默认取第一个用户
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

//        return (T) queryBuilder.unique();
    }

//    public static <T extends BaseUserToken> String getUserToken(Class<T> clazz) {
//        T user = getUser(clazz);
//        if (user != null) {
//            return user.getUserToken();
//        }
//        return null;
//    }

    /**
     * 将用户信息保存到数据库中
     */
    public static void saveUserInfoToDb(Object dataPojo) {
        AbstractDao dao = DaoUtil.getDaoSession().getDao(dataPojo.getClass());
        dao.deleteAll();        //删除用户信息表中的所有数据

        QueryBuilder queryBuilder = dao.queryBuilder();
        showLog(queryBuilder);

        dao.insert(dataPojo);       //新增用户信息

    }


    public static void saveNewUserInfoToDb(Object dataPojo, Object key) {
        AbstractDao dao = DaoUtil.getDaoSession().getDao(dataPojo.getClass());

        QueryBuilder queryBuilder = dao.queryBuilder();
        showLog(queryBuilder);

        //在数据库中查找是否有此数据项
        Object dbPojo = queryBuilder.where(dao.getPkProperty().eq(key)).unique();
        if (dbPojo != null) {       //更新数据
            dao.update(dataPojo);
        } else {        //保存数据
            dao.insert(dataPojo);
        }

    }
}