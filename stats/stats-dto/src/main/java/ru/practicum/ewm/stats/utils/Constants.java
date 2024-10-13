package ru.practicum.ewm.stats.utils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Constants {
    public static final ZoneId MSK_ZONE = ZoneId.of("Europe/Moscow");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(MSK_ZONE);
}
