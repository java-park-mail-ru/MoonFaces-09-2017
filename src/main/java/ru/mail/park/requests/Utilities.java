package ru.mail.park.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class Utilities {
    public static String makeJson(Object requestObject) {
        try {
            return new ObjectMapper().writeValueAsString(requestObject);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}