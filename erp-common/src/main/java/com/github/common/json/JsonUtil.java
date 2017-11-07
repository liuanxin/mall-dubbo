package com.github.common.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.common.util.A;
import com.github.liuanxin.page.model.PageList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

	private static final ObjectMapper BASIC = new BasicObjectMapper();

    /**
     * 用来渲染给前台的 json 映射器, 定义了一些自定义类的序列化规则, 没有反序列化规则
     */
    public static final ObjectMapper RENDER = new RenderObjectMapper();
    private static final ObjectMapper CONVERT = new ConvertObjectMapper();

	private static class BasicObjectMapper extends ObjectMapper {
		private BasicObjectMapper() {
			super();
            // 日期不用 utc 方式显示(utc 是一个整数值)
            // configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            // 时间格式
            // setDateFormat(new SimpleDateFormat(DateFormatType.YYYY_MM_DD_HH_MM_SS.getValue()));
            // 不确定值的枚举返回 null
			configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
			// 不确定的属性项上不要失败
			configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}
	}
    private static class RenderObjectMapper extends BasicObjectMapper {
        private RenderObjectMapper() {
            super();
            // 序列化 PageList 时只将总条数和当前页的数据返回. 否则可以使用 PageListJsonSerializer 这个现成的实现
            // registerModule(new SimpleModule().addSerializer(new PageListJsonSerializer(this)));
            registerModule(new SimpleModule().addSerializer(PageList.class, new JsonSerializer<PageList>() {
                @Override
                @SuppressWarnings("unchecked")
                public void serialize(PageList value, JsonGenerator gen, SerializerProvider sp) throws IOException {
                    gen.writeObject(A.maps(
                            "items", new ArrayList<>(value),
                            "total", value.getTotal()
                    ));
                }
            }));
        }
    }
    private static class ConvertObjectMapper extends ObjectMapper {
        private ConvertObjectMapper() {
            super();
            configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
    }

    /** 对象转换, 失败将会返回 null */
    public static <S,T> T convert(S source, Class<T> clazz) {
	    return toObjectNil(toJson(source), clazz);
    }
    /** 集合转换, 失败将会返回 null */
    public static <S,T> List<T> convertList(List<S> sourceList, Class<T> clazz) {
	    return toListNil(toJson(sourceList), clazz);
    }

	/** 对象转换成 json 字符串 */
	public static String toJson(Object obj) {
	    return toJson(BASIC, obj);
	}
    private static String toJson(ObjectMapper om, Object obj) {
        try {
            return om.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("object(" + obj + ") to json exception.", e);
        }
    }
    /** 对象转换成 json 字符串, 主要用来渲染到前台 */
    public static String toRender(Object obj) {
        return toJson(RENDER, obj);
    }

	/** 将 json 字符串转换为对象 */
	public static <T> T toObject(String json, Class<T> clazz) {
		try {
			return BASIC.readValue(json, clazz);
		} catch (Exception e) {
			throw new RuntimeException("json (" + json + ") to object(" + clazz.getName() + ") exception", e);
		}
	}
    /** 将 json 字符串转换为对象, 当转换异常时, 返回 null */
    public static <T> T toObjectNil(String json, Class<T> clazz) {
        try {
            return BASIC.readValue(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /** 将 json 字符串转换为指定的数组列表 */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        try {
            return BASIC.readValue(json, BASIC.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new RuntimeException("json(" + json + ") to list(" + clazz.getName() + ") exception.", e);
        }
    }
    /** 将 json 字符串转换为指定的数组列表 */
    public static <T> List<T> toListNil(String json, Class<T> clazz) {
        try {
            return BASIC.readValue(json, BASIC.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            return null;
        }
    }
}
