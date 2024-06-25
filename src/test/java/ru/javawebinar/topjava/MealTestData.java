package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
//    public static final int USER_ID = START_SEQ;
//    public static final int ADMIN_ID = START_SEQ + 1;
//    public static final int GUEST_ID = START_SEQ + 2;
    public static final int NOT_FOUND = 10;

    //meals of user with id = 100000
    public static final Meal meal1 = new Meal(100003,
            LocalDateTime.of(2020, 1,30, 10, 0, 0), "Завтрак", 500);
    public static final Meal meal2 = new Meal(100004,
            LocalDateTime.of(2020, 1,30, 13, 0, 0), "Обед", 1000);
    public static final Meal meal3 = new Meal(100005,
            LocalDateTime.of(2020, 1,30, 20, 0, 0), "Ужин", 500);
    public static final Meal meal4 = new Meal(100006,
            LocalDateTime.of(2020, 1,31, 0, 0, 0), "Еда на граничное значение", 100);
    public static final Meal meal5 = new Meal(100007,
            LocalDateTime.of(2020, 1,31, 10, 0, 0), "Завтрак", 1000);
    public static final Meal meal6 = new Meal(100008,
            LocalDateTime.of(2020, 1,31, 13, 0, 0), "Обед", 500);
    public static final Meal meal7 = new Meal(100009,
            LocalDateTime.of(2020, 1,31, 20, 0, 0), "Ужин", 410);
    //meals of user with id = 100001
    public static final Meal meal8 = new Meal(100010,
            LocalDateTime.of(2020, 1,31, 13, 0, 0), "Обед", 1500);
    public static final Meal meal9 = new Meal(100011,
            LocalDateTime.of(2020, 1,31, 20, 0, 0), "Ужин", 1410);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.now(), "New meal", 1555);
    }

    public static Meal getUpdated(Meal meal) {
        Meal updated = new Meal(meal);
        updated.setId(meal.getId());
        updated.setCalories(1);
        updated.setDescription("UpdatedDesc");
        updated.setDateTime(LocalDateTime.of(2024, 12, 31, 23,59, 59));
        return updated;
    }

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).usingRecursiveComparison().ignoringFields("registered", "roles").isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparatorIgnoringFields("registered", "roles").isEqualTo(expected);
    }
}
