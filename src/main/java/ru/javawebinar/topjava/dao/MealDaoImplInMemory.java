package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class MealDaoImplInMemory implements MealDao {
    private final AtomicLong maxId;
    private final ConcurrentMap<Long, Meal> meals;

    private static MealDaoImplInMemory instance;

    private MealDaoImplInMemory() {
        maxId = new AtomicLong(1L);
        meals = new ConcurrentHashMap<>();
    }

    public static MealDaoImplInMemory getInstance() {
        if (instance == null) {
            instance = new MealDaoImplInMemory();
        }
        return instance;
    }

    @Override
    public Meal create(Meal meal) {
        Meal newMeal = null;
        if (meal.getId() == null) {
            Long idMeal = getNextId();
            newMeal = new Meal(idMeal, meal.getDateTime(), meal.getDescription(), meal.getCalories());
            meals.put(idMeal, newMeal);
        }
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
