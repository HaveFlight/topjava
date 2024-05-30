package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExcess;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)
        );

        List<UserMealWithExcess> mealsTo = filteredByCycles(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        mealsTo.forEach(System.out::println);

        System.out.println(filteredByStreams(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExcess> filteredByCycles(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // based on https://stackoverflow.com/questions/4553624/hashmap-get-put-complexity
        // assume the complexity of get/put methods of HashMap implementation is O(1)

        // calculate calories consumed per day
        Map<LocalDate, Integer> caloriesPerDayStatistics = new HashMap<>();
        meals.forEach(meal -> {
            LocalDate date = meal.getDateTime().toLocalDate();
            Integer cal = caloriesPerDayStatistics.getOrDefault(date, 0);
            caloriesPerDayStatistics.put(date, cal + meal.getCalories());
        });

        List<UserMealWithExcess> resultMeals = new ArrayList<>();
        meals.forEach(meal -> {
            // filter meals out by given time-interval
            if (TimeUtil.isBetweenHalfOpen(meal.getDateTime().toLocalTime(), startTime, endTime)) {

                boolean excess = caloriesPerDayStatistics.get(meal.getDateTime().toLocalDate()) > caloriesPerDay;
                UserMealWithExcess userMealWithExcess = convertUserMeal(meal, excess);

                resultMeals.add(userMealWithExcess);
            }
        });
        return resultMeals;
    }

    public static List<UserMealWithExcess> filteredByStreams(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        // based on https://stackoverflow.com/questions/4553624/hashmap-get-put-complexity
        // assume the complexity of get/put methods of HashMap implementation is O(1)

        // calculate calories consumed per day
        Map<LocalDate, Integer> caloriesPerDayStatistics = meals.stream()
                .collect(
                        Collectors.groupingBy(
                                meal -> meal.getDateTime().toLocalDate(),
                                Collectors.summingInt(UserMeal::getCalories)));

        return meals.stream()
                .map(meal -> convertUserMeal(
                        meal,
                        caloriesPerDayStatistics.get(meal.getDateTime().toLocalDate()) > caloriesPerDay))
                .filter(userMealWithExcess ->
                        TimeUtil.isBetweenHalfOpen(userMealWithExcess.getDateTime().toLocalTime(), startTime, endTime))
                .collect(Collectors.toList());
    }

    /**
     * Вспомогательный метод для преобразования объекта класса UserMeal в объект класса UserMealWithExcess
     * @param userMeal - объект, свойства которого нужно использовать при создании результирующего объекта в @return
     * @param excess - флаг, показывающий имеется ли превышение калорий создаваемого объекта @return
     * @return - объект класса UserMealWithExcess, созданный с использованием данных из входный параметров
     */
    private static UserMealWithExcess convertUserMeal(UserMeal userMeal, boolean excess) {
        return new UserMealWithExcess(
                userMeal.getDateTime(),
                userMeal.getDescription(),
                userMeal.getCalories(),
                excess);
    }
}