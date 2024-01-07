<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%@include file="../../../head.jsp"%>
<%@include file="../../../header.jsp"%>
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
                            <th class="col-1">Otwarcie</th>
                            <th class="col-1">Zamknięcie</th>
                            <th class="col-1">LOT</th>
                            <th class="col-1">$</th>
                            <th class="col-1">Strona</th>
                            <th class="col-1">Dźwignia</th>
                            <th class="col-1">Czas</th>
                            <th class="col-1">Profit</th>
                            <th class="col-2 center">AKCJE</th>
                        </tr>
                        </thead>
                        <tbody class="text-color-lighter">
                        <c:forEach var="history" items="${historyOrders}" varStatus="stat">
                                <tr class="d-flex">
                                    <td><c:out value="${stat.index  + 1}"/></td>
                                    <td class="col-1"><c:out value="${history.symbol}"/></td>
                                    <td class="col-1"><c:out value="${history.entry}"/></td>
                                    <td class="col-1"><c:out value="${history.close}"/></td>
                                    <td class="col-1"><c:out value="${history.lot}"/></td>
                                    <td class="col-1"><c:out value="${history.amount}"/></td>
                                    <td class="col-1"><c:out value="${history.side}"/></td>
                                    <td class="col-1"><c:out value="${history.leverage}"/></td>
                                    <td class="col-1 dateTime"><c:out value="${history.created}"/></td>
                                    <c:if test="${history.realizedPln >= 0}">
                                        <td class="col-1" style="color: green"><c:out value="${history.realizedPln}"/></td>
                                    </c:if>
                                    <c:if test="${history.realizedPln < 0}">
                                        <td class="col-1" style="color: red"><c:out value="${history.realizedPln}"/></td>
                                    </c:if>
                                </tr>
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
<%@include file="../../../footer.jsp"%>