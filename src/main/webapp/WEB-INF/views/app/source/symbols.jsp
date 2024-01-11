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
                            <%--                            <th class="col-1">Win</th>--%>
                            <%--                            <th class="col-1">Loss</th>--%>
                            <%--                            <th class="col-1">Accuracy</th>--%>
                            <%--                            <th class="col-1">Bot</th>--%>
                            <th class="col-2 center">AKCJE</th>
                        </tr>
                        </thead>
                        <tbody class="text-color-lighter">
                        <c:forEach var="symbol" items="${symbols}" varStatus="stat">
                            <tr class="d-flex">
                                <td><c:out value="${stat.index + 1}"/></td>
                                <td class="col-1"><c:out value="${symbol.name}"/></td>
                                <td class="col-2 d-flex align-items-center justify-content-center flex-wrap">
                                    <sec:authorize access="hasRole('ADMIN')">
                                        <form method="post" action="/app/source/delete-symbol">
                                            <input type="hidden" name="sourceId" value="${sourceId}"/>
                                            <input type="hidden" name="symbolId" value="${symbol.id}"/>
                                            <button type="submit" class="btn btn-danger rounded-0 text-light m-1">Usuń
                                            </button>
                                            <sec:csrfInput/>
                                        </form>
                                    </sec:authorize>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>


                <form action="/app/source/symboladd" method="post">
                    <label for="symbol">Wybierz symbol:</label>
                    <select id="symbol" name="symbolId">
                        <c:forEach var="symbol" items="${symbolList}">
                            <option value="${symbol.id}">${symbol.name}</option>
                        </c:forEach>
                    </select>
                    <input type="hidden" name="sourceId" value="${sourceId}"/>
                    <input type="submit" value="Dodaj">
                    <sec:csrfInput/>
                </form>
            </div>
        </div>
    </div>
</section>

</body>

</html>
<%@include file="../../footer.jsp" %>

