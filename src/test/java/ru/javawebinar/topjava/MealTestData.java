package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {
//    public static final int USER_ID = START_SEQ;
//    public static final int ADMIN_ID = START_SEQ + 1;
//    public static final int GUEST_ID = START_SEQ + 2;
    public static final int NOT_FOUND = 10;

    //meals of user with id = 100000
    public static final Meal userMeal1 = new Meal(START_SEQ + 3, LocalDateTime.of(2020, 1,30, 10, 0, 0), "Завтрак", 500);
    public static final Meal userMeal2 = new Meal(START_SEQ + 4, LocalDateTime.of(2020, 1,30, 13, 0, 0), "Обед", 1000);
    public static final Meal userMeal3 = new Meal(START_SEQ + 5, LocalDateTime.of(2020, 1,30, 20, 0, 0), "Ужин", 500);
    public static final Meal userMeal4 = new Meal(START_SEQ + 6, LocalDateTime.of(2020, 1,31, 0, 0, 0), "Еда на граничное значение", 100);
    public static final Meal userMeal5 = new Meal(START_SEQ + 7, LocalDateTime.of(2020, 1,31, 10, 0, 0), "Завтрак", 1000);
    public static final Meal userMeal6 = new Meal(START_SEQ + 8, LocalDateTime.of(2020, 1,31, 13, 0, 0), "Обед", 500);
    public static final Meal userMeal7 = new Meal(START_SEQ + 9, LocalDateTime.of(2020, 1,31, 20, 0, 0), "Ужин", 410);
    //meals of user with id = 100001
    public static final Meal adminMeal8 = new Meal(START_SEQ + 10, LocalDateTime.of(2020, 1,31, 13, 0, 0), "Обед", 1500);
    public static final Meal adminMeal9 = new Meal(START_SEQ + 11, LocalDateTime.of(2020, 1,31, 20, 0, 0), "Ужин", 1410);

    public static Meal getNew() {
        return new Meal(null, LocalDateTime.of(2026, 12, 31, 1, 1, 1), "New meal", 1555);
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
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... expected) {
        assertMatch(actual, Arrays.asList(expected));
    }

    public static void assertMatch(Iterable<Meal> actual, Iterable<Meal> expected) {
        assertThat(actual).usingRecursiveFieldByFieldElementComparator().isEqualTo(expected);
    }
}
