package com.mtl.aio;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 说明:通过反射获取实例
 *
 * @作者 莫天龙
 * @时间 2019/12/16 11:54
 */
public class ClassFactory {

    public static <T> T newInstance(Class<T> clazz){
        try {
            Constructor<T> constructor = clazz.getConstructor();
            T t = constructor.newInstance();
            return t;
        } catch (NoSuchMethodException e) {
            new RuntimeException("初始化类["+clazz.getName()+"]失败！请提供默认构造方法！", e);
        } catch (Exception e) {
            new RuntimeException("初始化类["+clazz.getName()+"]失败！", e);
        }
        return null;
    }
}
