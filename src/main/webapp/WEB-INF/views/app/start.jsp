<%--
  Created by IntelliJ IDEA.
  User: damian
  Date: 20.09.2023
  Time: 22:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%@include file="head.jsp"%>
<body>
<%@include file="header.jsp"%>
<%@include file="Dashboard.jsp"%>
<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <a class="navbar-brand" href="#">Moja Strona</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
        <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNav">
        <ul class="navbar-nav">
            <li class="nav-item active">
                <a class="nav-link" href="#">Strona główna <span class="sr-only">(current)</span></a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="#">O nas</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="#">Usługi</a>
            </li>
            <li class="nav-item">
                <a class="nav-link" href="#">Kontakt</a>
            </li>
        </ul>
    </div>
</nav>

<div class="container-fluid">
    <div class="row">
        <!-- Menu po lewej stronie -->
        <nav class="col-md-2 d-none d-md-block bg-light sidebar">
            <div class="position-sticky">
                <ul class="nav flex-column">
                    <li class="nav-item">
                        <a class="nav-link active" href="#">
                            Menu 1
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">
                            Menu 2
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">
                            Menu 3
                        </a>
                    </li>
                </ul>
            </div>
        </nav>

        <!-- Główna treść strony -->
        <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4">
            <h1 class="mt-4">Witaj na stronie startowej</h1>
            <p>To jest przykładowy widok strony startowej z menu po lewej stronie i górnym paskiem nawigacyjnym.</p>
        </main>
    </div>
</div>

<!-- Dodaj linki do plików JavaScript Bootstrapa (jQuery i Popper.js są wymagane) -->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.4/dist/umd/popper.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

</body>

</html>
<%@include file="footer.jsp"%>