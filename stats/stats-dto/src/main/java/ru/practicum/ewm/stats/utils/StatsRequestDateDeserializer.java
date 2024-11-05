package ru.practicum.ewm.stats.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static ru.practicum.ewm.stats.utils.Constants.DATE_TIME_FORMATTER;

public class StatsRequestDateDeserializer extends JsonDeserializer<Instant> {
    @Override
    public Instant deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String decodedLocalDateTimeStr = URLDecoder.decode(jp.getText(), StandardCharsets.UTF_8);
        return DATE_TIME_FORMATTER.parse(decodedLocalDateTimeStr, Instant::from);
    }
}
