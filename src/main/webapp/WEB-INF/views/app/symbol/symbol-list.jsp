<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%--
  Created by IntelliJ IDEA.
  User: damian
  Date: 20.09.2023
  Time: 22:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%@include file="../../head.jsp"%>
<%@include file="../../header.jsp"%>
<body>

<section class="dashboard-section">
    <div class="row dashboard-nowrap">
        <%@include file="../dashboard.jsp" %>

        <div class="m-4 p-3 width-medium">
            <div class="dashboard-content border-dashed p-3 m-4 view-height">
                <div class="row border-bottom border-3 p-1 m-1">
                    <div class="col noPadding">
                        <h3 class="color-header text-uppercase">LISTA KRYPTOWALUT</h3>
                    </div>
                    <div class="col d-flex justify-content-end mb-2 noPadding">
                        <form:form method="post" action="/app/binance/add-symbol" modelAttribute="symbol">
                            <form:hidden path="id"/>
                            Symbol: <form:input path="name"/>
                            <form:button class="btn btn-success rounded-0 pt-0 pb-0 pr-4 pl-4">Dodaj symbol</form:button>
                            <br>
                            <form:errors path="name" cssStyle="color: red"/>
                        </form:form>

                    </div>
                </div>

                <div class="schedules-content">
                    <table class="table border-bottom">
                        <thead>
                        <tr class="d-flex">
                            <th>Lp</th>
                            <th class="col-1">Symbol</th>
                            <th class="col-2">Cena</th>
                            <th class="col-2">Otwarte</th>
                            <th class="col-2">Min.</th>
                            <th class="col-2">Max.</th>
                            <th class="col-1">%</th>
                            <th class="col-2 center">AKCJE</th>
                        </tr>
                        </thead>
                        <tbody class="text-color-lighter">
                        <c:forEach var="symbol" items="${symbols}" varStatus="stat">
                            <tr class="d-flex">
                                <td><c:out value="${stat.index  + 1}"/></td>
                                <td class="col-1"><c:out value="${symbol.symbol}"/></td>
                                <td class="col-2"><c:out value="${symbol.markPrice}"/></td>
                                <td class="col-2"><c:out value="${symbol.open}"/></td>
                                <td class="col-2"><c:out value="${symbol.lowPrice}"/></td>
                                <td class="col-2"><c:out value="${symbol.highPrice}"/></td>
                                <c:if test="${symbol.dayChangePercent >= 0}">
                                    <td class="col-1 color-price-increase"><c:out value="${symbol.dayChangePercent}"/></td>
                                </c:if>
                                <c:if test="${symbol.dayChangePercent < 0}">
                                    <td class="col-1 color-price-degrease"><c:out value="${symbol.dayChangePercent}"/></td>
                                </c:if>
                                <td class="col-2 d-flex align-items-center justify-content-center flex-wrap">
                                    <c:if test="${user.getRole() ne 'ROLE_ADMIN'}">
                                        <a href="/app/binance/delete-symbol?symbolId=${symbol.getId()}"
                                           class="btn btn-danger rounded-0 text-light m-1">Usuń</a>
                                    </c:if>
                                    <a href="/app/binance/open?symbolId=${symbol.getId()}"
                                       class="btn btn-info rounded-0 text-light m-1">Otwórz</a>
                                    <a href="/app/binance/alert?symbolId=${symbol.getId()}"
                                       class="btn btn-info rounded-0 text-light m-1">Alert</a>
                                    <c:if test="${symbol.open == true}">
                                        <a href="/app/binance/close?symbolId=${symbol.getId()}"
                                           class="btn btn-danger rounded-0 text-light m-1">Zamknij</a>
                                        <a href="/app/binance/edit?symbolId=${symbol.getId()}"
                                           class="btn btn-warning rounded-0 text-light m-1">Edytuj</a>
                                    </c:if>

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
<%@include file="../../footer.jsp"%>