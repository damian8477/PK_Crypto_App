<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css"
          integrity="sha384-GJzZqFGwb1QTTN6wy59ffF1BuGJpLSa9DkKMp0DgiMDm4iYMj70gZWKYbI706tWS" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js"
            integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo"
            crossorigin="anonymous"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.6/umd/popper.min.js"
            integrity="sha384-wHAiFfRlMFy6i5SRaxvfOCifBUQy1xHdJ/yoi7FRNXMRBu5WHdZYu1hA6ZOblgut"
            crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/js/bootstrap.min.js"
            integrity="sha384-B0UglyR+jN6CkvvICOB2joaf5I4l3gm9GU6Hc1og6Ls7i6U/mkkaduKaBhlAXv9k"
            crossorigin="anonymous"></script>
</head>
<%@include file="../head.jsp" %>
<%@include file="header.jsp" %>
<body>
<section class="dashboard-section">
    <div class="row dashboard-nowrap">
        <div class="m-4 p-3 width-medium">
            <div class="dashboard-content border-dashed p-3 m-4 view-height">
                <div class="container">
                    <div class="row" style="margin-top: 40px">
                        <div class="col-1"></div>
                        <div class="col-10" style="padding-bottom: 20px"><h2>Rejestracja</h2></div>
                        <div class="col-1"></div>
                    </div>
                    <div class="row">
                        <div class="col-1"></div>
                        <div class="col-6">
                            <form:form method="post" action="/register" modelAttribute="user">
                                <div class="form-group">
                                    <label for="username">Nazwa użytkownika</label>
                                    <form:input path="username" class="form-control"
                                           placeholder="Podaj nazwę użytkownika"/><form:errors path="username"/>
                                </div>
                                <div class="form-group">
                                    <label for="email">Email</label>
                                    <form:input path="email" class="form-control"
                                           placeholder="Podaj email"/><form:errors path="email"/>
                                </div>
                                <div class="form-group">
                                    <label for="firstName">Imię</label>
                                    <form:input path="firstName" class="form-control"
                                                placeholder="Podaj imię"/><form:errors path="firstName"/>
                                </div>
                                <div class="form-group">
                                    <label for="lastName">Nazwisko</label>
                                    <form:input path="lastName" class="form-control"
                                                placeholder="Podaj nazwisko"/><form:errors path="lastName"/>
                                </div>
                                <div class="form-group">
                                    <label for="password">Hasło</label>
                                    <form:password path="password" class="form-control"
                                                placeholder="Podaj hasło"/><form:errors path="password"/>
                                </div>
                                <form:button class="btn btn-primary" type="submit">Zarejestruj</form:button>
                                <form:button class="btn btn-secondary" type="reset">Wyczyść dane</form:button>
                                <sec:csrfInput/>
                            </form:form>
                        </div>
                        <div class="col-5"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
</body>
<%@include file="footer.jsp" %>
</html>