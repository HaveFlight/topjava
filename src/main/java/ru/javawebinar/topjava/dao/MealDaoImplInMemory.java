package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;

import java.util.*;

public class MealDaoImplInMemory implements MealDao {
    private final static Object maxIdLock = new Object();
    private static Long maxId = 0L;
    private static final List<Meal> meals = new LinkedList<>();

    private Long getNextId() {
        synchronized (maxIdLock) {
            return maxId++;
        }
    }

    @Override
    public void create(Meal meal) {
        if (meal.getId() == null) {
            Long idMeal = getNextId();
            synchronized (meals) {
                meals.add(new Meal(idMeal, meal.getDateTime(), meal.getDescription(), meal.getCalories()));
            }
        }
    }

    @Override
    public Meal read(Long id) {
        return meals.stream().filter(meal -> Objects.equals(meal.getId(), id)).findFirst().orElse(null);
    }

    @Override
    public List<Meal> readAll() {
        return meals;
    }

    @Override
    public void update(Meal meal) {
        synchronized (meals) {
            // if the Meal's fields were mutable we can modify existing meal instead of replacing it
            if (meals.removeIf(existingMeal -> Objects.equals(existingMeal.getId(), meal.getId()))) {
                meals.add(meal);
            }
        }
    }

    @Override
    public void delete(Long id) {
        synchronized (meals) {
            meals.removeIf(existingMeal -> Objects.equals(existingMeal.getId(), id));
        }
    }
}
