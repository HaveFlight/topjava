package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDate;
import java.util.List;

// TODO add userId
public interface MealRepository {
    // null if updated meal does not belong to userId
    Meal save(Integer userId, Meal meal);

    // false if meal does not belong to userId
    boolean delete(Integer userId, int mealId);

    // null if meal does not belong to userId
    Meal get(Integer userId, int mealId);

    // ORDERED dateTime desc
    List<Meal> getAll(Integer userId);
    // ORDERED dateTime desc
    List<Meal> getAll(Integer userId, LocalDate startDate, LocalDate endDate);
}
