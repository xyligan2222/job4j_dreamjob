<%--
  Created by IntelliJ IDEA.
  User: user
  Date: 23.12.2020
  Time: 20:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ page import="model.Photo" %>
<%@ page import="model.Candidate" %>
<%@ page import="store.PsqlStore" %>
<%@ page import="model.City" %>
<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
          integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"
            integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n" crossorigin="anonymous"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"
            integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"
            integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script>
        function validate() {
            if ($('#name').val() === '' || $('#city').val() === '' ||
                $('#name').val() === null || $('#city').val() === null) {
                alert('Заполните все данные');
                return false;
            }
            return true;
        }

        $(document).ready(function () {
            $.ajax({
                type: 'GET',
                url: 'http://localhost:8080/job4j_dreamjob/city',
                dataType: 'json'
            }).done(function (data) {
                $.each(data, function (index, value) {
                    $('#city').append('<option value="' + value.id + '">' + value.name + '</option>')
                });
                $('#city').val(new URLSearchParams(window.location.search).get("city"));
            }).fail(function (err) {
                alert(err);
            });
        });
    </script>
    <title>Работа мечты</title>
</head>
<body>
<%
    String id = request.getParameter("id");
    String idPhoto = request.getParameter("photoId");
    String idCity = request.getParameter("cityId");
    Candidate candidate = new Candidate(0, "", 0);
    if (id != null) {
        candidate = PsqlStore.instOf().findByIdCandidate(Integer.valueOf(id));
    }
    Photo photo = new Photo(0, "");
    if (idPhoto != null) {
        photo = PsqlStore.instOf().findByIdPhoto(Integer.valueOf(idPhoto));
    }
    City city= new City(0, "");
    if (idCity != null) {
        city = PsqlStore.instOf().findCityById(Integer.valueOf(idCity));
        System.out.println("cc");
        System.out.println(city);
    }
%>
<div class="container pt-3">
    <ul class="nav">
        <li class="nav-item">
            <a class="nav-link" href="<%=request.getContextPath()%>/index.jsp">Главная</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="<%=request.getContextPath()%>/posts.do">Вакансии</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="<%=request.getContextPath()%>/candidates.do">Кандидаты</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="<%=request.getContextPath()%>/post/edit.jsp">Добавить вакансию</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="<%=request.getContextPath()%>/candidate/edit.jsp">Добавить кандидата</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="<%=request.getContextPath()%>/login.jsp"> Выйти</a>
        </li>
    </ul>
    <div class="row">
        <div class="card" style="width: 100%">
            <div class="card-header">
                <% if (id == null) { %>
                Новый кандидат.
                <% } else { %>
                Редактирование кандидата.
                <% } %>
            </div>
            <div class="card-body">
                <form action="<%=request.getContextPath()%>/candidates.do?id=<%=candidate.getId()%>" method="post">
                    <div class="form-group">
                        <label>ФИО</label>
                        <input type="text" class="form-control" name="name" value="<%=candidate.getName()%>" id="name" placeholder="Введите ФИО">
                    </div>
                    <div class="form-group">
                        <label>Город</label>
                        <select class="form-control" id="city" name="cityId" value="<%=city.getName()%>" >
                            <option disabled>Выберите город</option>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-primary" onclick="return validate()">Сохранить</button>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
