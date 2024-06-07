package ru.javawebinar.topjava.web;

import com.sun.istack.internal.Nullable;
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

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final int CALORIES_PER_DAY_THRESHOLD = 2000;
    private static final String CREATE_OR_EDIT = "/meal.jsp";
    private static final String LIST = "/meals.jsp";
    private static final Logger log = getLogger(MealServlet.class);
    private final MealDao meals;

    public MealServlet() {
        meals = new MealDaoImplInMemory();
        MealsUtil.mockData().forEach(meals::create);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("redirect to meals");

        String forward = LIST;
        String action = request.getParameter("action");

        if (action != null) {
            switch(action) {
                case "edit":
                    Long id = Long.parseLong(request.getParameter("id")) ;
                    Meal meal = meals.read(id);
                    request.setAttribute("meal", meal);
                case "insert":
                    forward = CREATE_OR_EDIT;
                    break;
                case "delete":
                    Long idToDelete = Long.parseLong(request.getParameter("id")) ;
                    meals.delete(idToDelete);
                    response.sendRedirect("meals");
                    return;
            }
        } else {
            List<MealTo> mealTos = MealsUtil.filteredByStreams(meals.readAll(), CALORIES_PER_DAY_THRESHOLD);
            request.setAttribute("meals", mealTos);
        }

        request.getRequestDispatcher(forward).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doPost(req, resp);
        String desc = req.getParameter("description");
        int calories = Integer.parseInt(req.getParameter("calories"));
        LocalDateTime dt = LocalDateTime.parse(req.getParameter("dateTime"));

        if (req.getParameter("id") == null || req.getParameter("id").isEmpty()) {
            meals.create(new Meal(null, dt, desc, calories));
        } else {
            Long id =  Long.parseLong(req.getParameter("id"));
            meals.update(new Meal(id, dt, desc, calories));
        }

        List<MealTo> mealTos = MealsUtil.filteredByStreams(meals.readAll(), CALORIES_PER_DAY_THRESHOLD);
        req.setAttribute("meals", mealTos);
        req.getRequestDispatcher(LIST).forward(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        super.doDelete(req, resp);
        Long idToDelete = Long.parseLong(req.getParameter("id")) ;
        meals.delete(idToDelete);
    }
}
