package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.DateTimeUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    // key is userId
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.meals.forEach(meal -> { save(1, meal); });
        MealsUtil.meals2.forEach(meal -> { save(2, meal); });
    }

    @Override
    public Meal save(Integer userId, Meal meal) {
        Map<Integer, Meal> userMeals = repository.compute(userId, (k, v) -> v == null ? new ConcurrentHashMap<>() : v);
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            userMeals.put(meal.getId(), meal);
            return meal;
        }
        // TODO: check if meal with the same mealId belongs to another user
        // handle case: update, but not present in storage
        return userMeals.computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    @Override
    public boolean delete(Integer userId, int mealId) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        return userMeals != null && !userMeals.isEmpty() && userMeals.remove(mealId) != null;
    }

    @Override
    public Meal get(Integer userId, int mealId) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        return userMeals != null ? userMeals.get(mealId) : null;
    }

    @Override
    public List<Meal> getAll(Integer userId) {
        return getFilteredByPredicate(userId, meal -> true);
    }

    @Override
    public List<Meal> getAll(Integer userId,
                             LocalDate startDate,
                             LocalDate endDate) {
        return getFilteredByPredicate(userId, meal -> DateTimeUtil.isBetweenHalfOpen(
                meal.getDateTime(),
                startDate == null ? LocalDateTime.MIN : startDate.atStartOfDay(),
                endDate == null ? LocalDateTime.MAX : endDate.plusDays(1L).atStartOfDay()
        ));
    }

    private List<Meal> getFilteredByPredicate(Integer userId, Predicate<Meal> filter) {
        Map<Integer, Meal> userMeals = repository.get(userId);
        return userMeals == null ? Collections.emptyList() :
                userMeals.values().stream()
                        .filter(filter)
                        .sorted(Comparator
                                .comparing(Meal::getDateTime)
                                .reversed())
                        .collect(Collectors.toList());
    }

}

