<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
<%@include file="header.jsp"%>
<body>
<%@include file="Dashboard.jsp"%>

<body>
<a href="<c:url value="/logout" />">Logout</a>
</body>

<!-- Dodaj linki do plików JavaScript Bootstrapa (jQuery i Popper.js są wymagane) -->
<%--<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>--%>
<%--<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.4/dist/umd/popper.min.js"></script>--%>
<%--<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>--%>
</body>

</html>
<%@include file="footer.jsp"%>