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
                        <h3 class="color-header text-uppercase">Statystyki</h3>
                    </div>
                </div>
                <form action="/app/statistic/source-date" method="get">
                    <label for="startDate">Data początkowa:</label>
                    <input type="date" id="startDate" name="startDate">
                    <label for="stopDate">Data końcowa:</label>
                    <input type="date" id="stopDate" name="stopDate">
                    <input type="submit" value="Odśwież">
                </form>

                <div class="schedules-content">
                    <div class="schedules-content">
                        <div class="schedules-content-header">
                            <div class="form-group row">
                                <span class="col-sm-2 label-size col-form-label">
                                    Nazwa źródła
                                </span>
                                <div class="col-sm-10">
                                    <p class="schedules-text"><c:out value="${source.name}" default="null"/></p>
                                </div>
                            </div>
                            <div class="form-group row">
                                <span class="col-sm-2 label-size col-form-label">
                                    Accuracy
                                </span>
                                <div class="col-sm-10">
                                    <p class="schedules-text">
                                        <c:out value="${sourceStat.accuracy}" default="null"/>
                                    </p>
                                </div>
                            </div>
                            <div class="form-group row">
                                <span class="col-sm-2 label-size col-form-label">
                                    Count win trade
                                </span>
                                <div class="col-sm-10">
                                    <p class="schedules-text">
                                        <c:out value="${sourceStat.countWin}" default="null"/>
                                    </p>
                                </div>
                            </div>
                            <div class="form-group row">
                                <span class="col-sm-2 label-size col-form-label">
                                    Count trade
                                </span>
                                <div class="col-sm-10">
                                    <p class="schedules-text">
                                        <c:out value="${sourceStat.countTrade}" default="null"/>
                                    </p>
                                </div>
                            </div>
                        </div>
                        <table class="table">
                            <thead>
                            <tr class="d-flex">
                                <th class="col-1">Symbol</th>
                                <th class="col-1">Accuracy</th>
                                <th class="col-1">Win pcs</th>
                                <th class="col-1">Trade pcs</th>
                                <th class="col-1">Akcje</th>
                            </tr>
                            </thead>
                            <tbody class="text-color-lighter">
                            <c:forEach var="symbol" items="${sourceStat.symbolStat}">
                                <tr class="d-flex">
                                    <td class="col-1">${symbol.cryptoName}</td>
                                    <c:if test="${symbol.accuracy >= 75}">
                                        <td class="col-1 color-price-increase"><c:out
                                                value="${symbol.accuracy}"/></td>
                                    </c:if>
                                    <c:if test="${symbol.accuracy < 75}">
                                        <td class="col-1 color-price-degrease"><c:out
                                                value="${symbol.accuracy}"/></td>
                                    </c:if>
                                    <td class="col-1">${symbol.countWin}</td>
                                    <td class="col-1">${symbol.countTrade}</td>
                                    <td class="col-1 center">
                                        <a href="/app/statistic/symbol-details?sourceId=${source.id}&symbolName=${symbol.cryptoName}"
                                           class="btn btn-danger rounded-0 text-light m-1">Szczegóły</a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>


                </div>
                <table class="table">
                    <thead>
                    <tr class="d-flex">
                        <th class="col-1">Zmiana</th>
                        <th class="col-1">Accuracy</th>
                        <th class="col-1">Win pcs</th>
                        <th class="col-1">Trade pcs</th>
                    </tr>
                    </thead>
                    <tbody class="text-color-lighter">
                    <c:forEach var="shift" items="${sourceStat.shiftTrades}">
                        <tr class="d-flex">
                            <td class="col-1">${shift.shift}</td>
                            <c:if test="${shift.accuracy >= 75}">
                                <td class="col-1 color-price-increase"><c:out
                                        value="${shift.accuracy}"/></td>
                            </c:if>
                            <c:if test="${shift.accuracy < 75}">
                                <td class="col-1 color-price-degrease"><c:out
                                        value="${shift.accuracy}"/></td>
                            </c:if>
                            <td class="col-1">${shift.countWin}</td>
                            <td class="col-1">${shift.countTrade}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>

                <br>
                <table class="table">
                    <thead>
                    <tr class="d-flex">
                        <th class="col-1">Godzina</th>
                        <th class="col-1">Accuracy</th>
                        <th class="col-1">Win pcs</th>
                        <th class="col-1">Trade pcs</th>
                    </tr>
                    </thead>
                    <tbody class="text-color-lighter">
                    <c:forEach var="shift" items="${sourceStat.hourTrades}">
                        <tr class="d-flex">
                            <td class="col-1">${shift.shift}</td>
                            <c:if test="${shift.accuracy >= 75}">
                                <td class="col-1 color-price-increase"><c:out
                                        value="${shift.accuracy}"/></td>
                            </c:if>
                            <c:if test="${shift.accuracy < 75}">
                                <td class="col-1 color-price-degrease"><c:out
                                        value="${shift.accuracy}"/></td>
                            </c:if>
                            <td class="col-1">${shift.countWin}</td>
                            <td class="col-1">${shift.countTrade}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    </div>
    </div>
</section>

</body>

</html>
<%@include file="../../footer.jsp" %>
