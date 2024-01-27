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
<section class="dashboard-section">
    <div class="row dashboard-nowrap">
        <%@include file="../dashboard.jsp" %>

        <div class="m-4 p-3 width-medium">
            <div class="dashboard-content border-dashed p-3 m-4 view-height">
                <div class="row border-bottom border-3 p-1 m-1">
                    <div class="col noPadding">
                        <h3 class="color-header text-uppercase">Wybierz statystyke</h3>
                    </div>

                </div>
                <div class="schedules-content">
                    <form action="/app/statistic/source" method="get">
                        <select id="source" name="sourceId">
                            <c:forEach var="source" items="${sourceList}">
                                <option value="${source.id}">${source.name}</option>
                            </c:forEach>
                        </select>
                        <label>Statystyki bota</label>
                        <input type="checkbox" name="userBot">
                        <input type="submit" value="Wybierz" class="btn btn-success rounded-0 pt-0 pb-0 pr-4 pl-4">
                        <sec:csrfInput/>
                    </form>
                </div>
            </div>
        </div>
    </div>
</section>

</body>

</html>
<%@include file="../../footer.jsp" %>
