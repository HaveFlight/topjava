package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.Collection;

public interface MealDao {
    Meal create(Meal meal);
    Meal read(long id);
    Collection<Meal> readAll();
    Meal update(Meal meal);
    void delete(long id);
}
