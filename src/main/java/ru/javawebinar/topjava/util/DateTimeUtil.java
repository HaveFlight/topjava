package ru.javawebinar.topjava.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateTimeUtil {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static<T extends Comparable<T>> boolean isBetweenHalfOpen(T ldt, T startDateTime, T endDateTime) {
        return ldt.compareTo(startDateTime) >= 0 && ldt.compareTo(endDateTime) < 0;
    }

    public static String toString(LocalDateTime ldt) {
        return ldt == null ? "" : ldt.format(DATE_TIME_FORMATTER);
    }

    public static LocalDate tryParseLocalDate(String localDate) {
        try {
            return localDate == null || localDate.isEmpty() ? null : LocalDate.parse(localDate);
        }
        catch (DateTimeParseException e) {
            return null;
        }
    }
    public static LocalTime tryParseLocalTime(String localTime) {
        try {
            return localTime == null || localTime.isEmpty() ? null : LocalTime.parse(localTime);
        }
        catch (DateTimeParseException e) {
            return null;
        }
    }
}

