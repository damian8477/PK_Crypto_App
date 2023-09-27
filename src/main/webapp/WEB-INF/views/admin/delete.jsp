<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%@include file="../head.jsp" %>
<%@include file="../header.jsp" %>
<body>
<section class="dashboard-section">
    <div class="row dashboard-nowrap">
        <%@include file="../app/Dashboard.jsp" %>
        <div class="m-4 p-3 width-medium text-color-darker">
            <div class="dashboard-content border-dashed p-3 m-4 view-height">
                <div class="mt-4 ml-4 mr-4">
                    <div class="container mt-5">
                        <h2>Potwierdź usunięcie</h2>
                        <p>Czy na pewno chcesz to usunąć ${user.username}?</p>
                        <form:form action="/admin/deletet" method="post">
                            <input type="hidden" name="userId" value="${user.id}">
                            <button type="submit" class="btn btn-danger">Tak, usuń</button>
                            <a href="/admin/user-list" class="btn btn-secondary">Anuluj</a>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

</body>
</html>
<%@include file="../footer.jsp" %>

