<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%@include file="../../head.jsp" %>
<%@include file="../../header.jsp" %>
<body>
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<script>
    $(document).ready(function () {
        $(".short-column").each(function () {
            var text = $(this).text();
            $(this).text(text.slice(10) + '.'.repeat(5));
        });
    });
</script>
<section class="dashboard-section">
    <div class="row dashboard-nowrap">
        <%@include file="../dashboard.jsp" %>

        <div class="m-4 p-3 width-medium">
            <div class="dashboard-content border-dashed p-3 m-4 view-height">
                <div class="row border-bottom border-3 p-1 m-1">
                    <div class="col noPadding">
                        <h3 class="color-header text-uppercase">Lista Źródeł</h3>
                    </div>
                    <div>
                        <a href="/app/source/add" class="btn btn-success rounded-0 pt-0 pb-0 pr-4 pl-4">Dodaj źródło</a>
                    </div>
                </div>

                <div class="schedules-content">
                    <table class="table border-bottom">
                        <thead>
                        <tr class="d-flex">
                            <th>Lp</th>
                            <th class="col-1">Nazwa</th>
                            <th class="col-1">Opis</th>
                            <th class="col-1">Admin</th>
                            <th class="col-1">BE %</th>
                            <th class="col-1">BE aktywny</th>
                            <th class="col-1">TP %</th>
                            <th class="col-1">TP aktywny</th>
                            <th class="col-1">SL %</th>
                            <th class="col-1">SL aktywny</th>
                            <th class="col-2 center">AKCJE</th>
                        </tr>
                        </thead>
                        <tbody class="text-color-lighter">
                        <c:forEach var="source" items="${sources}" varStatus="stat">
                            <tr class="d-flex">
                                <td><c:out value="${stat.index + 1}"/></td>
                                <td class="col-1"><c:out value="${source.name}"/></td>
                                <td class="col-1 short-column"><c:out value="${source.description}"/></td>
                                <td class="col-1"><c:out value="${source.adminStrategy}"/></td>
                                <td class="col-1"><c:out value="${source.bePercent}"/></td>
                                <td class="col-1"><c:out value="${source.walkingStopLoss}"/></td>
                                <td class="col-1"><c:out value="${source.basicTpPercent}"/></td>
                                <td class="col-1"><c:out value="${source.activeBasicTp}"/></td>
                                <td class="col-1"><c:out value="${source.basicSlPercent}"/></td>
                                <td class="col-1"><c:out value="${source.activeBasicSl}"/></td>
                                <td class="col-2 d-flex align-items-center justify-content-center flex-wrap">
                                    <sec:authorize access="hasRole('ADMIN')">
                                        <a href="/app/source/delete?sourceId=${source.getId()}"
                                           class="btn btn-danger rounded-0 text-light m-1">Usuń</a>
                                        <a href="/app/source/edit?sourceId=${source.getId()}"
                                           class="btn btn-info rounded-0 text-light m-1">Edytuj</a>
                                        <a href="/app/source/symbols?sourceId=${source.getId()}"
                                           class="btn btn-info rounded-0 text-light m-1">Symbole</a>
                                    </sec:authorize>
                                    <a href="/app/source/add?sourceId=${source.id}"
                                       class="btn btn-info rounded-0 text-light m-1">Dodaj</a>
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
<%@include file="../../footer.jsp" %>

