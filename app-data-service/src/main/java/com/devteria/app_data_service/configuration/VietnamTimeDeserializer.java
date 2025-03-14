package com.devteria.app_data_service.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class VietnamTimeDeserializer extends JsonDeserializer<Instant> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateTimeString = p.getText(); // Read input timestamp as string
        return LocalDateTime.parse(dateTimeString, formatter)
                .atZone(ZoneId.of("Asia/Bangkok")) // Convert from GMT+7
                .toInstant(); // Convert to UTC
    }
}
