package ru.javawebinar.topjava.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static<T extends Comparable<T>> boolean isBetweenHalfOpen(T ldt, T start, T end) {
        return ldt.compareTo(start) >= 0 && ldt.compareTo(end) < 0;
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }

    public static LocalDate tryParseLocalDate(String localDate) {
        return localDate == null || localDate.isEmpty() ? null : LocalDate.parse(localDate);
    }
    public static LocalTime tryParseLocalTime(String localTime) {
        return localTime == null || localTime.isEmpty() ? null : LocalTime.parse(localTime);
    }
}

