<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
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
                        <h3 class="color-header text-uppercase">LISTA AKTYWNYCH ZLECEŃ</h3>
                    </div>
                </div>

                <div class="schedules-content">
                    <table class="table border-bottom">
                        <thead>
                        <tr class="d-flex">
                            <th>Lp</th>
                            <th class="col-1">Symbol</th>
                            <th class="col-1">Cena</th>
                            <th class="col-1">Tp</th>
                            <th class="col-1">Sl</th>
                            <th class="col-1">LOT</th>
                            <th class="col-1">$</th>
                            <th class="col-1">Strona</th>
                            <th class="col-1">Profit</th>
                            <th class="col-1">Dźwignia</th>
                            <th class="col-1">Czas</th>
                            <th class="col-2 center">AKCJE</th>
                        </tr>
                        </thead>
                        <tbody class="text-color-lighter">
                        <c:forEach var="order" items="${orders}" varStatus="stat">
                            <c:if test="${order.appOrder == true}">
                                <tr class="d-flex">
                                    <td><c:out value="${stat.index  + 1}"/></td>
                                    <td class="col-1"><c:out value="${order.symbolName}"/></td>
                                    <td class="col-1"><c:out value="${order.entry}"/></td>
                                    <td class="col-1"><c:out value="${order.tp}"/></td>
                                    <td class="col-1"><c:out value="${order.sl}"/></td>
                                    <td class="col-1"><c:out value="${order.lot}"/></td>
                                    <td class="col-1"><c:out value="${order.amount}"/></td>
                                    <td class="col-1"><c:out value="${order.positionSide}"/></td>
                                    <c:if test="${order.profitProcent >= 0}">
                                        <td class="col-1" style="color: green"><c:out
                                                value="${order.profitProcent}"/></td>
                                    </c:if>
                                    <c:if test="${order.profitProcent < 0}">
                                        <td class="col-1" style="color: red"><c:out
                                                value="${order.profitProcent}"/></td>
                                    </c:if>
                                    <td class="col-1"><c:out value="${order.leverage}"/></td>
                                    <td class="col-1 dateTime"><c:out value="${order.created}"/></td>
                                    <td class="col-2 d-flex align-items-center justify-content-center flex-wrap">
                                        <a href="/app/orders/close-order?orderId=${order.id}"
                                           class="btn btn-danger rounded-0 text-light m-1">Zamknij</a>
                                        <a href="/app/orders/edit-order?orderId=${order.id}"
                                           class="btn btn-info rounded-0 text-light m-1">Edytuj</a>
                                    </td>
                                </tr>
                            </c:if>
                        </c:forEach>
                        </tbody>
                    </table>
                    <br><br><br>
                    <div class="row border-bottom border-3 p-1 m-1">
                        <div class="col noPadding">
                            <h3 class="color-header text-uppercase">LISTA OTWARTYCH ZLECEŃ PO ZA APLIKACJĄ</h3>
                        </div>
                    </div>
                    <table class="table border-bottom">
                        <thead>
                        <tr class="d-flex">
                            <th>Lp</th>
                            <th class="col-1">Symbol</th>
                            <th class="col-1">Cena</th>
                            <th class="col-1">Tp</th>
                            <th class="col-1">Sl</th>
                            <th class="col-1">LOT</th>
                            <th class="col-1">$</th>
                            <th class="col-1">Strona</th>
                            <th class="col-1">Profit</th>
                            <th class="col-1">Dźwignia</th>
                            <th class="col-1">Czas</th>
                            <th class="col-2 center">AKCJE</th>
                        </tr>
                        </thead>
                        <tbody class="text-color-lighter">
                        <c:forEach var="order" items="${orders}" varStatus="stat">
                            <c:if test="${order.appOrder == false}">
                                <tr class="d-flex">
                                    <td><c:out value="${stat.index  + 1}"/></td>
                                    <td class="col-1"><c:out value="${order.symbolName}"/></td>
                                    <td class="col-1"><c:out value="${order.entry}"/></td>
                                    <td class="col-1"><c:out value="${order.tp}"/></td>
                                    <td class="col-1"><c:out value="${order.sl}"/></td>
                                    <td class="col-1"><c:out value="${order.lot}"/></td>
                                    <td class="col-1"><c:out value="${order.amount}"/></td>
                                    <td class="col-1"><c:out value="${order.side}"/></td>
                                    <c:if test="${order.profitProcent >= 0}">
                                        <td class="col-1" style="color: green"><c:out
                                                value="${order.profitProcent}"/></td>
                                    </c:if>
                                    <c:if test="${order.profitProcent < 0}">
                                        <td class="col-1" style="color: red"><c:out
                                                value="${order.profitProcent}"/></td>
                                    </c:if>
                                    <td class="col-1"><c:out value="${order.leverage}"/></td>
                                    <td class="col-1"><c:out value="${order.created}"/></td>
                                    <td class="col-2 d-flex align-items-center justify-content-center flex-wrap">
                                        <a href="/app/orders/close-order?symbol=${order.symbolName}"
                                           class="btn btn-danger rounded-0 text-light m-1">Zamknij</a>
                                        <a href="/app/orders/edit-order?symbol=${order.symbolName}"
                                           class="btn btn-info rounded-0 text-light m-1">Edytuj</a>
                                    </td>
                                </tr>
                            </c:if>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</section>
<script>
    let dateTimeElement = document.getElementsByClassName('dateTime');
    let dateFormatter = new Intl.DateTimeFormat('en-GB', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
    });

    for (var i = 0; i < dateTimeElement.length; i++) {
        var originalDateTimeString = dateTimeElement[i].innerText;
        var originalDateTime = new Date(originalDateTimeString);
        var formattedDateTime = dateFormatter.format(originalDateTime);
        dateTimeElement[i].innerText = formattedDateTime;
    }


</script>
</body>

</html>
<%@include file="../../../footer.jsp" %>