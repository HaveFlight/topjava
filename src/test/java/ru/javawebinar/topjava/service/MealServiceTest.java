package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.*;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void getExisting() {
        Meal actualMeal = service.get(userMeal1.getId(), UserTestData.USER_ID);

        assertMatch(actualMeal, userMeal1);
    }

    @Test
    public void getAbsent() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_FOUND, UserTestData.USER_ID));
    }

    @Test
    public void getByAnotherUser()  {
        assertThrows(NotFoundException.class, () -> service.get(userMeal1.getId(), UserTestData.ADMIN_ID));
    }

    @Test
    public void createByExistingUser() {
        Meal newMeal1 = getNew();
        Meal created = service.create(newMeal1, UserTestData.USER_ID);
        int newId = created.getId();

        Meal expectedMeal = getNew();
        expectedMeal.setId(newId);

        assertMatch(created, expectedMeal);

        Meal mealJustInserted = service.get(newId, UserTestData.USER_ID);
        mealJustInserted.setId(null);

        assertMatch(mealJustInserted, expectedMeal);
    }

    @Test
    public void createDuplicateByDateTime() {
        Meal duplicateMeal = getNew();
        duplicateMeal.setDateTime(userMeal1.getDateTime());

        assertThrows(DuplicateKeyException.class, () -> service.create(duplicateMeal, UserTestData.USER_ID));
    }

    @Test
    public void deleteExisting() {
        service.delete(userMeal1.getId(), UserTestData.USER_ID);

        assertThrows(NotFoundException.class, () -> service.get(userMeal1.getId(), UserTestData.USER_ID));
    }

    @Test
    public void deleteExistingByAnotherUser() {
        assertThrows(NotFoundException.class, () -> service.delete(userMeal1.getId(), UserTestData.ADMIN_ID));
    }

    @Test
    public void deleteAbsent() {
        assertThrows(NotFoundException.class, () -> service.delete(MealTestData.NOT_FOUND, UserTestData.USER_ID));
    }

    @Test
    public void updateExisting() {
        int existingMealId = userMeal1.getId();
        Meal updatedMeal = getUpdated(userMeal1);
        Meal expectedMeal = getUpdated(userMeal1);

        service.update(updatedMeal, UserTestData.USER_ID);

        assertMatch(service.get(existingMealId, UserTestData.USER_ID), expectedMeal);
    }

    @Test
    public void updateExistingByAnotherUser() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(userMeal1), UserTestData.ADMIN_ID));
    }

    @Test
    public void getAllByExistingUser() {
        List<Meal> actualMeals = service.getAll(UserTestData.USER_ID);
        List<Meal> mealsExpected = Stream.of(userMeal1, userMeal2, userMeal3, userMeal4, userMeal5, userMeal6, userMeal7)
                .sorted(Comparator
                        .comparing(Meal::getDateTime)
                        .reversed())
                .collect(Collectors.toList());

        assertMatch(actualMeals, mealsExpected);
    }

    @Test
    public void getAllByAbsentUser() {
        List<Meal> actualMeals = service.getAll(UserTestData.NOT_FOUND);

        assertMatch(actualMeals, Collections.emptyList());
    }

    @Test
    public void getBetweenInclusiveBelowLeftBound() {
        List<Meal> actualMeals = service.getBetweenInclusive(
                LocalDate.MIN,
                LocalDate.of(2020, 1,29),
                UserTestData.USER_ID);

        assertMatch(actualMeals, Collections.emptyList());
    }

    @Test
    public void getBetweenInclusiveExactLeftBound() {
        List<Meal> actualMeals = service.getBetweenInclusive(
                LocalDate.MIN,
                LocalDate.of(2020, 1,30),
                UserTestData.USER_ID);
        List<Meal> mealsExpected = Stream.of(userMeal1, userMeal2, userMeal3)
                .sorted(Comparator
                        .comparing(Meal::getDateTime)
                        .reversed())
                .collect(Collectors.toList());

        assertMatch(actualMeals, mealsExpected);
    }

    @Test
    public void getBetweenInclusiveExactRightBound() {
        List<Meal> actualMeals = service.getBetweenInclusive(
                LocalDate.of(2020, 1,31),
                LocalDate.of(3000,1,1),
                UserTestData.USER_ID);
        List<Meal> mealsExpected = Stream.of(userMeal4, userMeal5, userMeal6, userMeal7)
                .sorted(Comparator
                        .comparing(Meal::getDateTime)
                        .reversed())
                .collect(Collectors.toList());

        assertMatch(actualMeals, mealsExpected);
    }
    @Test
    public void getBetweenInclusiveAboveRightBound() {
        List<Meal> actualMeals = service.getBetweenInclusive(
                LocalDate.of(2020, 2,1),
                LocalDate.of(3000,1,1),
                UserTestData.USER_ID);

        assertMatch(actualMeals, Collections.emptyList());
    }

    @Test
    public void getBetweenInclusiveInTheMiddle() {
        List<Meal> actualMeals = service.getBetweenInclusive(
                LocalDate.of(2020, 1,31),
                LocalDate.of(2020, 1,31),
                UserTestData.USER_ID);
        List<Meal> mealsExpected = Stream.of(userMeal4, userMeal5, userMeal6, userMeal7)
                .sorted(Comparator
                        .comparing(Meal::getDateTime)
                        .reversed())
                .collect(Collectors.toList());

        assertMatch(actualMeals, mealsExpected);
    }

}