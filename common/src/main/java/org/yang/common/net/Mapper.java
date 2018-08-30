package org.yang.common.net;

import com.google.common.base.Optional;
import com.google.gson.Gson;

import org.reactivestreams.Subscriber;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by Gxy on 2017/4/5
 */

public class Mapper<T extends ResponseBody> implements Function<T, Object> {
    private Subscriber mSubscriber;

    public Mapper(Subscriber mSubscriber) {
        this.mSubscriber = mSubscriber;
    }

    //操作结果集
    @Override
    public Object apply(@NonNull T res) {

        try {
            Optional<Class> actualType = getActualType();

            if (!actualType.isPresent()) {      //没有指定返回的类型
                return res;
            }

            Class clazz = actualType.get();
            if (clazz == res.getClass()) {      //返回的类型为T
                return res;
            }

            Gson gson = new Gson();
            return gson.fromJson(res.charStream(), clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return res;
        }

    }


    /**
     * 规避范型擦除机制,获取请求数据要求的返回类型
     *
     * @return
     */
    private Optional<Class> getActualType() {
        if (mSubscriber == null) {
            return null;
        }

        Type[] genericInterfaces = mSubscriber.getClass().getGenericInterfaces();
        if (genericInterfaces.length > 0) {
            ParameterizedType parameterizedType;
            try {
                parameterizedType = (ParameterizedType) genericInterfaces[0];

                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                return Optional.fromNullable((Class) actualTypeArguments[0]);
            } catch (ClassCastException e) {
                return Optional.absent();
            }
        } else {
            return Optional.absent();
        }
    }

}
