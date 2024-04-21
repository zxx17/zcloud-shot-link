package com.zskj.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Xinxuan Zhuo
 * @version 2024/4/21
 * <p>
 * JsonUtil json工具类
 * </p>
 */
@Slf4j
public class JsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {

        //设置可用单引号
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        //序列化的时候序列对象的所有属性
        MAPPER.setSerializationInclusion(JsonInclude.Include.ALWAYS);

        //反序列化的时候如果多了其他属性,不抛出异常
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //如果是空对象的时候,不抛异常
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        //取消时间的转化格式,默认是时间戳,可以取消,同时需要设置要表现的时间格式
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }



    /**
     * 对象转为Json字符串
     * @param obj
     * @return
     */
    public static String obj2Json(Object obj) {
        String jsonStr = null;
        try {
            jsonStr = MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("json格式化异常:{}",e.getMessage());
        }
        return jsonStr;
    }

    /**
     * json字符串转为对象
     * @param jsonStr
     * @param beanType
     * @return
     */
    public static <T> T json2Obj(String jsonStr, Class<T> beanType) {
        T obj = null;
        try {
            obj = MAPPER.readValue(jsonStr, beanType);
        } catch (Exception e){
            
            log.error("json格式化异常:{}",e.getMessage());
        }
        return obj;
    }


    /**
     * json数据转换成pojo对象list
     * @param jsonData
     * @param beanType
     * @return
     */
    public static <T> List<T> json2List(String jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        try {
            List<T> list = MAPPER.readValue(jsonData, javaType);
            return list;
        } catch (Exception e) {
            log.error("json格式化异常:{}",e.getMessage());
        }
        return null;
    }

    /**
     * 对象转为byte数组
     * @param obj
     * @return
     */
    public static byte[] obj2Bytes(Object obj) {
        byte[] byteArr = null;
        try {
            byteArr = MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            log.error("json格式化异常:{}",e.getMessage());
        }
        return byteArr;
    }



    /**
     * byte数组转为对象
     * @param byteArr
     * @param beanType
     * @return
     */
    public static <T> T bytes2Obj(byte[] byteArr, Class<T> beanType) {
        T obj = null;
        try {
            obj = MAPPER.readValue(byteArr, beanType);
        } catch (Exception e) {
            log.error("json格式化异常:{}",e.getMessage());
        }
        return obj;
    }
    
}
