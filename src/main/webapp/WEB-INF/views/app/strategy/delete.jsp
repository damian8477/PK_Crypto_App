<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%@include file="../../head.jsp" %>
<%@include file="../../header.jsp" %>
<body>
<body>
<section class="dashboard-section">
    <div class="row dashboard-nowrap">
        <%@include file="../dashboard.jsp" %>
        <div class="m-4 p-3 width-medium text-color-darker">
            <div class="dashboard-content border-dashed p-3 m-4 view-height">
                <div class="mt-4 ml-4 mr-4">
                    <div class="container mt-5">
                        <h2>Potwierdź usunięcie</h2>
                        <p>Czy na pewno chcesz usunąć wybraną strategie?</p>
                        <form:form action="/app/strategy/delete" method="post">
                            <input type="hidden" name="strategyId" value="${strategyId}">
                            <button type="submit" class="btn btn-danger">Tak, usuń</button>
                            <a href="/app/strategy/list" class="btn btn-secondary">Anuluj</a>
                            <sec:csrfInput/>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

</body>

<%@include file="../../footer.jsp" %>
</html>

