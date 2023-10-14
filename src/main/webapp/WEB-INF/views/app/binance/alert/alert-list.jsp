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
<%@include file="../../../head.jsp" %>
<%@include file="../../../header.jsp" %>
<body>

<section class="dashboard-section">
    <div class="row dashboard-nowrap">
        <%@include file="../../dashboard.jsp" %>

        <div class="m-4 p-3 width-medium">
            <div class="dashboard-content border-dashed p-3 m-4 view-height">
                <div class="row border-bottom border-3 p-1 m-1">
                    <div class="col noPadding">
                        <h3 class="color-header text-uppercase">LISTA ALERTÓW</h3>
                    </div>
                </div>
                <div class="schedules-content">
                    <table class="table border-bottom">
<%--                        <thead>--%>
                        <tr class="d-flex">
                            <th>Lp</th>
                            <th class="col-1 center">Cena</th>
                            <th class="col-1 center">Long/Short</th>
                            <th class="col-1 center">Kierunek</th>
                            <th class="col-2 center">AKCJE</th>
                        </tr>
                        <tbody class="text-color-lighter">
                        <c:forEach var="symbolAlerts" items="${alerts}" varStatus="stat">
                            <c:forEach var="key" items="${symbolAlerts.keySet()}">
                                <tr  class="d-flex">
                                    <td class="col-4 bigger center"><c:out value="${key}"/></td>
                                </tr>
                            </c:forEach>
                            <c:forEach var="alertList" items="${symbolAlerts.values()}" varStatus="stat2">
                                <c:forEach var="alert" items="${alertList}">
                                    <tr class="d-flex">
                                        <td><c:out value="${stat.index + stat2.index  + 1}"/></td>
                                        <td class="col-1 center"><c:out value="${alert.price}"/></td>
                                        <td class="col-1 center"><c:out value="${alert.positionSide}"/></td>
                                        <td class="col-1 center"><c:out value="${alert.direction}"/></td>
                                        <td class="col-2 d-flex align-items-center justify-content-center flex-wrap">
                                            <a href="/app/alerts/delete?alertId=${alert.getId()}"
                                               class="btn btn-danger rounded-0 text-light m-1">Usuń</a>
                                            <a href="/app/alerts/edit?alertId=${alert.getId()}"
                                               class="btn btn-warning rounded-0 text-light m-1">Edytuj</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:forEach>
                        </c:forEach>
                        </tbody>

                    </table>
                </div>


                <%--                <div class="schedules-content">--%>
                <%--                    <table class="table border-bottom">--%>
                <%--                        <thead>--%>
                <%--                        <tr class="d-flex">--%>
                <%--                            <th>Lp</th>--%>
                <%--                            <th class="col-1">Symbol</th>--%>
                <%--                            <th class="col-2">Cena</th>--%>
                <%--                            <th class="col-2">Otwarte</th>--%>
                <%--                            <th class="col-2">Min.</th>--%>
                <%--                            <th class="col-2">Max.</th>--%>
                <%--                            <th class="col-1">%</th>--%>
                <%--                            <th class="col-2 center">AKCJE</th>--%>
                <%--                        </tr>--%>
                <%--                        </thead>--%>
                <%--                        <tbody class="text-color-lighter">--%>
                <%--                        <c:forEach var="symbol" items="${symbols}" varStatus="stat">--%>
                <%--               %--                                <td><c:out value="${stat.index  + 1}"/></td>&ndash;%&gt;--%>
                <%--                                <td class="col-1"><c:out value="${symbol.symbol}"/></td>--%>
                <%--                                <td class="col-2"><c:out value="${symbol.markPrice}"/></td>--%>
                <%--                                <td class="col-2"><c:out value="${symbol.open}"/></td>--%>
                <%--                                <td class="col-2"><c:out value="${symbol.lowPrice}"/></td>--%>
                <%--                                <td class="col-2"><c:out value="${symbol.highPrice}"/></td>--%>
                <%--                                <c:if test="${symbol.dayChangePercent >= 0}">--%>
                <%--                                    <td class="col-1 color-price-increase"><c:out value="${symbol.dayChangePercent}"/></td>--%>
                <%--                                </c:if>--%>
                <%--                                <c:if test="${symbol.dayChangePercent < 0}">--%>
                <%--                                    <td class="col-1 color-price-degrease"><c:out value="${symbol.dayChangePercent}"/></td>--%>
                <%--                                </c:if>--%>
                <%--                                <td class="col-2 d-flex align-items-center justify-content-center flex-wrap">--%>

                <%--                                    <sec:authorize access="hasRole('ROLE_ADMIN')">--%>
                <%--                                        <a href="/app/binance/delete-symbol?symbolId=${symbol.getId()}"--%>
                <%--                                           class="btn btn-danger rounded-0 text-light m-1">Usuń</a>--%>
                <%--                                    </sec:authorize>--%>
                <%--                                    <a href="/app/binance/open-symbol?symbolId=${symbol.getId()}"--%>
                <%--                                       class="btn btn-info rounded-0 text-light m-1">Otwórz</a>--%>
                <%--                                    <a href="/app/alerts/alert?symbolId=${symbol.getId()}"--%>
                <%--                                       class="btn btn-info rounded-0 text-light m-1">Alert</a>--%>
                <%--                                    <c:if test="${symbol.open == true}">--%>
                <%--                                        <a href="/app/binance/close?symbolId=${symbol.getId()}"--%>
                <%--                                           class="btn btn-danger rounded-0 text-light m-1">Zamknij</a>--%>
                <%--                                        <a href="/app/binance/edit?symbolId=${symbol.getId()}"--%>
                <%--                                           class="btn btn-warning rounded-0 text-light m-1">Edytuj</a>--%>
                <%--                                    </c:if>--%>

                <%--                                </td>--%>
                <%--                            </tr>--%>
                <%--                        </c:forEach>--%>
                <%--                        </tbody>--%>
                <%--                    </table>--%>
                <%--                </div>--%>
            </div>
        </div>
    </div>
</section>

</body>

</html>
<%@include file="../../../footer.jsp" %>