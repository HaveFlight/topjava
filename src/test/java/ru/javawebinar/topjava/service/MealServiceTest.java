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
        Meal meal = meal1;

        Meal actualMeal = service.get(meal.getId(), userId);

        assertMatch(actualMeal, meal);
    }

    @Test(expected = NotFoundException.class)
    public void getAbsent() {
        int userId = UserTestData.USER_ID;
        int absentMealId = NOT_FOUND;

        service.get(absentMealId, userId);
    }

    @Test(expected = NotFoundException.class)
    public void getByAnotherUser()  throws Exception {
        int userIdOwner = UserTestData.USER_ID;
        int userIdAnother = UserTestData.ADMIN_ID;
        Meal existingMeal = service.getAll(userIdOwner).stream().findFirst().orElse(null);
        if (existingMeal == null) {
            throw new Exception("Cannot find data for the test case");
        }

        service.get(existingMeal.getId(), userIdAnother);
    }


    @Test
    public void createByExistingUser() {
        int userId = UserTestData.USER_ID;

        Meal newMeal1 = getNew();
        Meal created = service.create(newMeal1, userId);
        Integer newId = created.getId();

        Meal newMeal2 = getNew();
        newMeal2.setId(newId);
        newMeal2.setDateTime(newMeal1.getDateTime());

        assertMatch(created, newMeal2);
        assertMatch(service.get(newId, userId), newMeal2);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void createByAbsentUser() {
        int userId = UserTestData.NOT_FOUND;

        service.create(getNew(), userId);
    }

    @Test(expected = DuplicateKeyException.class)
    public void createDuplicateByDateTime() throws Exception {
        int userId = UserTestData.USER_ID;
        Meal existingMeal = service.getAll(userId).stream().findFirst().orElse(null);

        if (existingMeal == null) {
            throw new Exception("Cannot find data for the test case");
        }

        Meal duplicateMeal = getNew();
        duplicateMeal.setDateTime(existingMeal.getDateTime());

        service.create(duplicateMeal, userId);
    }

    @Test(expected = NotFoundException.class)
    public void deleteExisting() throws Exception {
        int userId = UserTestData.USER_ID;
        Meal existingMeal = service.getAll(userId).stream().findFirst().orElse(null);

        if (existingMeal == null) {
            throw new Exception("Cannot find data for the test case");
        }

        service.delete(existingMeal.getId(), userId);

        service.get(existingMeal.getId(), userId);
    }

    @Test(expected = NotFoundException.class)
    public void deleteExistingByAnotherUser() throws Exception {
        int userIdOwner = UserTestData.USER_ID;
        int userIdAnother = UserTestData.ADMIN_ID;

        Meal existingMeal = service.getAll(userIdOwner).stream().findFirst().orElse(null);

        if (existingMeal == null) {
            throw new Exception("Cannot find data for the test case");
        }

        service.delete(existingMeal.getId(), userIdAnother);
    }


    @Test(expected = NotFoundException.class)
    public void deleteAbsent() throws Exception {
        int userId = UserTestData.USER_ID;
        boolean mealIsAbsent = false;
        try {
            service.get(MealTestData.NOT_FOUND, userId);
        }
        catch (NotFoundException exc) {
            mealIsAbsent = true;
        }

        if (!mealIsAbsent) {
            throw new Exception("Data doesn't suite the test case");
        }

        service.delete(MealTestData.NOT_FOUND, userId);
    }

    @Test
    public void updateExisting() throws Exception {
        int userId = UserTestData.USER_ID;
        Meal existingMeal = service.getAll(userId).stream().findFirst().orElse(null);
        if (existingMeal == null) {
            throw new Exception("Cannot find data for the test case");
        }
        int existingMealId = existingMeal.getId();
        Meal updatedMeal = getUpdated(existingMeal);

        service.update(updatedMeal, userId);

        assertMatch(service.get(existingMealId, userId), updatedMeal);
    }

    @Test(expected = NotFoundException.class)
    public void updateExistingByAnotherUser() throws Exception {
        int userIdOwner = UserTestData.USER_ID;
        int userIdAnother = UserTestData.ADMIN_ID;
        Meal existingMeal = service.getAll(userIdOwner).stream().findFirst().orElse(null);
        if (existingMeal == null) {
            throw new Exception("Cannot find data for the test case");
        }
        int existingMealId = existingMeal.getId();
        Meal updatedMeal = getUpdated(existingMeal);

        service.update(updatedMeal, userIdAnother);
    }

    @Test(expected = NotFoundException.class)
    public void updateAbsent() throws Exception {
        int userId = UserTestData.USER_ID;
        boolean mealIsAbsent = false;
        try {
            service.get(MealTestData.NOT_FOUND, userId);
        }
        catch (NotFoundException exc) {
            mealIsAbsent = true;
        }

        if (!mealIsAbsent) {
            throw new Exception("Data doesn't suite the test case");
        }

        Meal absentMeal = getNew();
        absentMeal.setId(MealTestData.NOT_FOUND);

        service.update(absentMeal, userId);
    }

    @Test
    public void getAllByExistingUser() {
        int userId = UserTestData.USER_ID;
        List<Meal> mealsActual = service.getAll(userId);
        List<Meal> mealsExpected = Stream.of(meal1, meal2, meal3, meal4, meal5, meal6, meal7)
                .sorted(Comparator
                        .comparing(Meal::getDateTime)
                        .reversed())
                .collect(Collectors.toList());

        assertMatch(mealsActual, mealsExpected);
    }

    @Test
    public void getAllByAbsentUser() {
        int userId = UserTestData.NOT_FOUND;
        List<Meal> mealsActual = service.getAll(userId);

        assertMatch(mealsActual, Collections.emptyList());
    }

    @Test
    public void getBetweenInclusiveBelowLeftBound() {
        int userId = UserTestData.USER_ID;
        List<Meal> mealsActual = service.getBetweenInclusive(
                LocalDate.MIN,
                LocalDate.of(2020, 1,29),
                userId);

        assertMatch(mealsActual, Collections.emptyList());
    }

    @Test
    public void getBetweenInclusiveExactLeftBound() {
        int userId = UserTestData.USER_ID;
        List<Meal> mealsActual = service.getBetweenInclusive(
                LocalDate.MIN,
                LocalDate.of(2020, 1,30),
                userId);
        List<Meal> mealsExpected = Stream.of(meal1, meal2, meal3)
                .sorted(Comparator
                        .comparing(Meal::getDateTime)
                        .reversed())
                .collect(Collectors.toList());

        assertMatch(mealsActual, mealsExpected);
    }

    @Test
    public void getBetweenInclusiveExactRightBound() {
        int userId = UserTestData.USER_ID;
        List<Meal> mealsActual = service.getBetweenInclusive(
                LocalDate.of(2020, 1,31),
                LocalDate.of(3000,1,1),
                userId);
        List<Meal> mealsExpected = Stream.of(meal4, meal5, meal6, meal7)
                .sorted(Comparator
                        .comparing(Meal::getDateTime)
                        .reversed())
                .collect(Collectors.toList());

        assertMatch(mealsActual, mealsExpected);
    }
    @Test
    public void getBetweenInclusiveAboveRightBound() {
        int userId = UserTestData.USER_ID;
        List<Meal> mealsActual = service.getBetweenInclusive(
                LocalDate.of(2020, 2,1),
                LocalDate.of(3000,1,1),
                userId);

        assertMatch(mealsActual, Collections.emptyList());
    }

    @Test
    public void getBetweenInclusiveInTheMiddle() {
        int userId = UserTestData.USER_ID;
        List<Meal> mealsActual = service.getBetweenInclusive(
                LocalDate.of(2020, 1,31),
                LocalDate.of(2020, 1,31),
                userId);
        List<Meal> mealsExpected = Stream.of(meal4, meal5, meal6, meal7)
                .sorted(Comparator
                        .comparing(Meal::getDateTime)
                        .reversed())
                .collect(Collectors.toList());

        assertMatch(mealsActual, mealsExpected);
    }

}