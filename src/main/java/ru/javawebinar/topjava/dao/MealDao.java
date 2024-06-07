package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealDao {
    void create(Meal meal);
    Meal read(Long id);
    List<Meal> readAll();
    void update(Meal meal);
    void delete(Long id);
}
