package com.vodacom.falcon.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T deserialize(String json, Class<T> clazz) {
        try {
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            LOGGER.error("Error deserializing object {} ", e.getMessage());
        }
        return null;
    }

    public static <T> T deserializeByTypeReference(String json, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            LOGGER.error("Error deserializing object {}", e.getMessage());
            return null;
        }
    }

    public static <T> String serialize(T object)  {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            LOGGER.error("Failed to serialize {}", e.getMessage());
        }
        return null;
    }
}
