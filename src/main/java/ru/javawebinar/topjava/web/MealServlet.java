package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.dao.MealDao;
import ru.javawebinar.topjava.dao.MealDaoImplInMemory;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final int CALORIES_PER_DAY_THRESHOLD = 2000;
    private static final String CREATE_OR_EDIT = "/meal.jsp";
    private static final String LIST = "/meals.jsp";
    private static final Logger log = getLogger(MealServlet.class);
    private MealDao meals;

    @Override
    public void init() throws ServletException {
        meals = new MealDaoImplInMemory();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String forward = LIST;
        String action = Objects.toString(request.getParameter("action"), "");

        switch (action) {
            case "edit":
                long id = Long.parseLong(request.getParameter("id"));
                Meal meal = meals.read(id);
                request.setAttribute("meal", meal);
                log.debug("redirect to edit meal {}", id);
                forward = CREATE_OR_EDIT;
                break;
            case "insert":
                log.debug("redirect to add new meal");
                forward = CREATE_OR_EDIT;
                break;
            case "delete":
                long idToDelete = Long.parseLong(request.getParameter("id"));
                log.debug("delete meal {}", idToDelete);
                meals.delete(idToDelete);
                response.sendRedirect("meals");
                return;
            default:
                log.debug("get meal list");
                List<MealTo> mealTos = MealsUtil.filteredByStreams(meals.readAll(), CALORIES_PER_DAY_THRESHOLD);
                request.setAttribute("meals", mealTos);
        }

        request.getRequestDispatcher(forward).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String description = req.getParameter("description");
        int calories = Integer.parseInt(req.getParameter("calories"));
        LocalDateTime dateTime = LocalDateTime.parse(req.getParameter("dateTime"));

        Meal savedMeal;
        String idParameter = req.getParameter("id");
        if (idParameter == null || idParameter.isEmpty()) {
            savedMeal = meals.create(new Meal(dateTime, description, calories));
        } else {
            Long id = Long.parseLong(idParameter);
            savedMeal = meals.update(new Meal(id, dateTime, description, calories));
        }
        if (savedMeal != null) {
            log.debug("meal {} POSTed successfully", savedMeal.getId());
        } else {
            log.debug("meal POST failed");
        }

        resp.sendRedirect("meals");
    }
}
