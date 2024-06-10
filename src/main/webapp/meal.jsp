<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html lang="ru">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Meal</title>
    </head>
    <body>


        <h3>
            <a href="index.html">Home</a>
        </h3>
        <hr>
        <h2>${meal.id == null ? 'Add meal' : 'Edit meal'}</h2>

        <form method="POST" action='meals' name="form-add-meal">
            <input type="hidden" name="id" value="${meal.id}" />
            <table>
                <tr>
                    <td>DateTime:</td>
                    <td>
                        <input type="datetime-local" name="dateTime" value="${meal.dateTime}" />
                    </td>
                </tr>
                <tr>
                    <td>Description:</td>
                    <td>
                        <input type="text" name="description" value="${meal.description}" />
                    </td>
                </tr>
                <tr>
                    <td>Calories:</td>
                    <td>
                        <input type="number" name="calories" value="${meal.calories}" />
                    </td>
                </tr>
            </table>
            <input type="submit" value="Save">
            <button onclick="window.history.back()" type="button">Cancel</button>
        </form>

    </body>
</html>