package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class MealDaoImplInMemory implements MealDao {
    private final AtomicLong maxId;
    private final ConcurrentMap<Long, Meal> meals;

    public MealDaoImplInMemory() {
        maxId = new AtomicLong(1L);
        meals = new ConcurrentHashMap<>();
    }

    @Override
    public Meal create(Meal meal) {
        Long idMeal = getNextId();
        Meal newMeal = new Meal(idMeal, meal.getDateTime(), meal.getDescription(), meal.getCalories());
        meals.put(idMeal, newMeal);
        return newMeal;
    }

    private Long getNextId() {
        return maxId.getAndIncrement();
    }

    @Override
    public Meal read(long id) {
        return meals.get(id);
    }

    @Override
    public Collection<Meal> readAll() {
        return meals.values();
    }

    @Override
    public Meal update(Meal meal) {
        return meals.computeIfPresent(meal.getId(), (k, v) -> meal);
    }

    @Override
    public void delete(long id) {
        meals.remove(id);
    }
}
