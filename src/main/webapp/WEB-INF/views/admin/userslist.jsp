<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%@include file="../head.jsp" %>
<%@include file="../header.jsp" %>
<body>
<section class="dashboard-section">
    <div class="row dashboard-nowrap">
        <%@include file="../app/Dashboard.jsp" %>

        <div class="m-4 p-3 width-medium">
            <div class="dashboard-content border-dashed p-3 m-4 view-height">
                <div class="row border-bottom border-3 p-1 m-1">
                    <div class="col noPadding">
                        <h3 class="color-header text-uppercase">LISTA UŻYTKOWNIKÓW</h3>
                    </div>
                </div>

                <div class="schedules-content">
                <table class="table border-bottom">
                    <thead>
                    <tr class="d-flex">
                        <th>Lp</th>
                        <th class="col-1">Nazwa</th>
                        <th class="col-2">Imię</th>
                        <th class="col-2">Nazwisko</th>
                        <th class="col-2">Aktywyny</th>
                        <th class="col-2">Rola</th>
                        <th class="col-2 center">AKCJE</th>
                    </tr>
                    </thead>
                    <tbody class="text-color-lighter">
                    <c:forEach var="user" items="${users}" varStatus="stat">
                        <tr class="d-flex">
                            <td><c:out value="${stat.index  + 1}"/></td>
                            <td class="col-1"><c:out value="${user.getUsername()}"/></td>
                            <td class="col-2"><c:out value="${user.getFirstName()}"/></td>
                            <td class="col-2"><c:out value="${user.getLastName()}"/></td>
                            <td class="col-2"><c:out value="${user.isActive()}"/></td>
                            <td class="col-2"><c:out value="${user.getRole()}"/></td>
                            <td class="col-2 d-flex align-items-center justify-content-center flex-wrap">
                                <c:if test="${user.getRole() ne 'ROLE_ADMIN'}">
                                    <a href="/admin/delete?userId=${user.getId()}"
                                       class="btn btn-danger rounded-0 text-light m-1">Usuń</a>
                                </c:if>
                                <a href="/admin/details?userId=${user.getId()}"
                                   class="btn btn-info rounded-0 text-light m-1">Szczegóły</a>
                                <a href="/admin/edit?userId=${user.getId()}"
                                   class="btn btn-warning rounded-0 text-light m-1">Edytuj</a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
            </div>
        </div>
    </div>
</section>
</body>
</html>
<%@include file="../footer.jsp" %>