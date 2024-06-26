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
        int userId = UserTestData.USER_ID;
        Meal meal = userMeal1;

        Meal actualMeal = service.get(meal.getId(), userId);

        assertMatch(actualMeal, meal);
    }

    @Test
    public void getAbsent() {
        int userId = UserTestData.USER_ID;
        int absentMealId = NOT_FOUND;

        assertThrows(NotFoundException.class, () -> service.get(absentMealId, userId));
    }

    @Test
    public void getByAnotherUser()  {
        assertThrows(NotFoundException.class, () -> service.get(userMeal1.getId(), UserTestData.ADMIN_ID));
    }

    @Test
    public void createByExistingUser() {
        int userId = UserTestData.USER_ID;

        Meal newMeal1 = getNew();
        Meal created = service.create(newMeal1, userId);
        int newId = created.getId();
        created.setId(null);

        Meal newMeal2 = getNew();
        newMeal2.setId(null);

        assertMatch(created, newMeal2);

        Meal mealJustInserted = service.get(newId, userId);
        mealJustInserted.setId(null);

        assertMatch(mealJustInserted, newMeal2);
    }

    @Test
    public void createByAbsentUser() {
        assertThrows(DataIntegrityViolationException.class, () -> service.create(getNew(), UserTestData.NOT_FOUND));
    }

    @Test
    public void createDuplicateByDateTime() {
        Meal duplicateMeal = getNew();
        duplicateMeal.setDateTime(userMeal1.getDateTime());

        assertThrows(DuplicateKeyException.class, () -> service.create(duplicateMeal, UserTestData.USER_ID));
    }

    @Test
    public void deleteExisting() {
        Meal existingMeal = userMeal1;

        service.delete(existingMeal.getId(), UserTestData.USER_ID);

        assertThrows(NotFoundException.class, () -> service.get(existingMeal.getId(), UserTestData.USER_ID));
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
        Meal existingMeal = userMeal1;
        int existingMealId = existingMeal.getId();
        Meal updatedMeal = getUpdated(existingMeal);
        Meal updatedMealCopy = getUpdated(existingMeal);

        service.update(updatedMeal, UserTestData.USER_ID);

        assertMatch(service.get(existingMealId, UserTestData.USER_ID), updatedMealCopy);
    }

    @Test
    public void updateExistingByAnotherUser() {
        Meal updatedMeal = getUpdated(userMeal1);

        assertThrows(NotFoundException.class, () -> service.update(updatedMeal, UserTestData.ADMIN_ID));
    }

    @Test
    public void updateAbsent() throws Exception {
        Meal absentMeal = getNew();
        absentMeal.setId(MealTestData.NOT_FOUND);

        assertThrows(NotFoundException.class, ()-> service.update(absentMeal, UserTestData.USER_ID));
    }

    @Test
    public void getAllByExistingUser() {
        List<Meal> mealsActual = service.getAll(UserTestData.USER_ID);
        List<Meal> mealsExpected = Stream.of(userMeal1, userMeal2, userMeal3, userMeal4, userMeal5, userMeal6, userMeal7)
                .sorted(Comparator
                        .comparing(Meal::getDateTime)
                        .reversed())
                .collect(Collectors.toList());

        assertMatch(mealsActual, mealsExpected);
    }

    @Test
    public void getAllByAbsentUser() {
        List<Meal> mealsActual = service.getAll(UserTestData.NOT_FOUND);

        assertMatch(mealsActual, Collections.emptyList());
    }

    @Test
    public void getBetweenInclusiveBelowLeftBound() {
        List<Meal> mealsActual = service.getBetweenInclusive(
                LocalDate.MIN,
                LocalDate.of(2020, 1,29),
                UserTestData.USER_ID);

        assertMatch(mealsActual, Collections.emptyList());
    }

    @Test
    public void getBetweenInclusiveExactLeftBound() {
        List<Meal> mealsActual = service.getBetweenInclusive(
                LocalDate.MIN,
                LocalDate.of(2020, 1,30),
                UserTestData.USER_ID);
        List<Meal> mealsExpected = Stream.of(userMeal1, userMeal2, userMeal3)
                .sorted(Comparator
                        .comparing(Meal::getDateTime)
                        .reversed())
                .collect(Collectors.toList());

        assertMatch(mealsActual, mealsExpected);
    }

    @Test
    public void getBetweenInclusiveExactRightBound() {
        List<Meal> mealsActual = service.getBetweenInclusive(
                LocalDate.of(2020, 1,31),
                LocalDate.of(3000,1,1),
                UserTestData.USER_ID);
        List<Meal> mealsExpected = Stream.of(userMeal4, userMeal5, userMeal6, userMeal7)
                .sorted(Comparator
                        .comparing(Meal::getDateTime)
                        .reversed())
                .collect(Collectors.toList());

        assertMatch(mealsActual, mealsExpected);
    }
    @Test
    public void getBetweenInclusiveAboveRightBound() {
        List<Meal> mealsActual = service.getBetweenInclusive(
                LocalDate.of(2020, 2,1),
                LocalDate.of(3000,1,1),
                UserTestData.USER_ID);

        assertMatch(mealsActual, Collections.emptyList());
    }

    @Test
    public void getBetweenInclusiveInTheMiddle() {
        List<Meal> mealsActual = service.getBetweenInclusive(
                LocalDate.of(2020, 1,31),
                LocalDate.of(2020, 1,31),
                UserTestData.USER_ID);
        List<Meal> mealsExpected = Stream.of(userMeal4, userMeal5, userMeal6, userMeal7)
                .sorted(Comparator
                        .comparing(Meal::getDateTime)
                        .reversed())
                .collect(Collectors.toList());

        assertMatch(mealsActual, mealsExpected);
    }

}