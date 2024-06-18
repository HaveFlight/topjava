package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;
import static ru.javawebinar.topjava.web.SecurityUtil.authUserId;

@Controller
public class MealRestController {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MealService service;

    public List<MealTo> getAll() {
        log.info("getAll");
        return MealsUtil.getTos(service.getAll(authUserId()), MealsUtil.DEFAULT_CALORIES_PER_DAY) ;
    }

    public List<MealTo> getAll(LocalDate startDate, LocalTime startTime, LocalDate endDate, LocalTime endTime) {
        log.info("getAll filtered");
        return MealsUtil.getFilteredTos(
                service.getAll(authUserId(), startDate, endDate),
                MealsUtil.DEFAULT_CALORIES_PER_DAY,
                startTime == null ? LocalTime.MIN : startTime,
                endTime == null ? LocalTime.MAX : endTime);
        // TODO: LocalTime variables are checked on null here,
        //  LocalDate ones - in nested logic inside repository implementation.
        //  Is that a correct approach?
    }

    public void delete(int mealId) {
        log.info("delete {}", mealId);
        service.delete(authUserId(), mealId);
    }

    public Meal get(int mealId) {
        log.info("get {}", mealId);
        return service.get(authUserId(), mealId);
    }

    public void create(Meal meal) {
        log.info("create {}", meal);
        checkNew(meal);
        service.create(authUserId(), meal);
    }

    public void update(Meal meal, int mealId) {
        log.info("update {} with id={}", meal, mealId);
        assureIdConsistent(meal, mealId);
        service.update(authUserId(), meal);
    }
}