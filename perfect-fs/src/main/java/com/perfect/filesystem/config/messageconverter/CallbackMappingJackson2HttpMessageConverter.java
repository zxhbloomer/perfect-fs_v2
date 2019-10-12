package com.perfect.filesystem.config.messageconverter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class CallbackMappingJackson2HttpMessageConverter extends FastJsonHttpMessageConverter {

    @Override
    public void write(Object o, Type type, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
//        System.out.println(JSON.toJSONString(o, SerializerFeature.DisableCircularReferenceDetect));
        // 中文乱码解决方案
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);//设定json格式且编码为UTF-8
        mediaTypes.add(MediaType.MULTIPART_FORM_DATA);
        mediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);
        mediaTypes.add(MediaType.valueOf("text/html;charset=UTF-8"));
        super.setSupportedMediaTypes(mediaTypes);

        /**
         *
         * QuoteFieldNames———-输出key时是否使用双引号,默认为true
         * WriteMapNullValue——–是否输出值为null的字段,默认为false
         * WriteNullNumberAsZero—-数值字段如果为null,输出为0,而非null
         * WriteNullListAsEmpty—–List字段如果为null,输出为[],而非null
         * WriteNullStringAsEmpty—字符类型字段如果为null,输出为”“,而非null
         * WriteNullBooleanAsFalse–Boolean字段如果为null,输出为false,而非null
         *
         * */
        super.getFastJsonConfig().setSerializerFeatures(SerializerFeature.DisableCircularReferenceDetect,
                                                        SerializerFeature.WriteNullStringAsEmpty,
                                                        SerializerFeature.WriteNullListAsEmpty,
                                                        SerializerFeature.WriteNullBooleanAsFalse,
                                                        SerializerFeature.PrettyFormat,
                                                        SerializerFeature.WriteNullNumberAsZero
                                                        );
        super.write(o, type, contentType, outputMessage);
    }

}