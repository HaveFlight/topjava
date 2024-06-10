<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html lang="ru">
    <style>
        table, tr, th, td {
            border: 1px solid black;
            border-collapse: collapse;
        }
    </style>
    <head>
        <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />
        <title>Meals</title>
    </head>
    <body>
        <h3>
            <a href="index.html">Home</a>
        </h3>
        <hr>
        <h2>Meals</h2>
        <p><a href="meals?action=insert">Add Meal</a></p>
        <table>
            <thead>
                <tr>
                    <th>Date</th>
                    <th>Description</th>
                    <th>Calories</th>
                    <th></th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${meals}" var="meal">

                    <c:set var="textColor" value="${meal.excess ? 'color:red;' : 'color:green;' }"/>

                    <tr style="${textColor}">
                        <td>${fn:replace(meal.dateTime,'T',' ')}</td>
                        <td>${meal.description}</td>
                        <td>${meal.calories}</td>

                        <td><a href="meals?action=edit&id=${meal.id}">Update</a></td>
                        <td><a href="meals?action=delete&id=${meal.id}">Delete</a></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </body>
</html>