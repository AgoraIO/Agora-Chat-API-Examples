package com.agora.app.utils;

import com.agora.app.model.ChatGptMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

@Slf4j
public class JsonUtil {

    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonUtil() {

    }

    public static String mapToJsonString(Object contentsObj) {
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        if (contentsObj == null) {
            return null;
        }

        try {
            return mapper.writeValueAsString(contentsObj);
        } catch (JsonProcessingException e) {
            log.error("Failed to write {} to string", contentsObj, e);
        }
        return StringUtils.EMPTY;
    }

    public static <T> T parse(String json, Class<T> clazz) {

        if (json == null) {
            return null;
        }

        try {
            return mapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Error parsing JSON, json:{}", json, e);
            return null;
        }
    }

    public static Map convert(ChatGptMessage message) {
        return mapper.convertValue(message, Map.class);
    }

}
