package com.bluecc.hubs.common;

import com.google.gson.Gson;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class FunctionTest {
    public Function<String, List<String>> split(){
        return e -> Arrays.asList(e.split(","));
    }

    public Function<List<String>, String> merge(){
        return e -> String.join(",", e);
    }

    @Test
    void testReturnType() throws NoSuchMethodException {
        //通过反射获取到方法
        Method declaredMethod = FunctionTest.class.getDeclaredMethod("split");
        //获取返回值的类型
        Type genericReturnType = declaredMethod.getGenericReturnType();
        //System.out.println(genericReturnType);
        //获取返回值的泛型参数
        if (genericReturnType instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
            for (Type type : actualTypeArguments) {
                System.out.println("type " + type);
            }
        }
    }

    @Test
    void testInputType() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Gson gson=new Gson();
        String jsonList=gson.toJson(Lists.newArrayList("first", "second"));
        Method declaredMethod = FunctionTest.class.getDeclaredMethod("merge");
        Type genericReturnType = declaredMethod.getGenericReturnType();
        if (genericReturnType instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
            Type inputType=actualTypeArguments[0];
            // > [first, second]
            System.out.println("> "+gson.fromJson(jsonList, inputType));
            Function<Object, Object> f=(Function<Object, Object>)declaredMethod.invoke(new FunctionTest());
            Object r=f.apply(gson.fromJson(jsonList, inputType));
            // < first,second
            System.out.println("< "+r);
        }
    }
}
