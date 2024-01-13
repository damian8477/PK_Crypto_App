<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%@include file="../../head.jsp" %>
<%@include file="../../header.jsp" %>
<body>
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<script>
    $(document).ready(function () {
        $(".key-column, .secret-column").each(function () {
            var text = $(this).text();
            $(this).text('*'.repeat(5) + text.slice(-4));
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
                        <h3 class="color-header text-uppercase">LISTA USTAWIEŃ</h3>
                    </div>
                    <div>
                        <a href="/app/user/edit" class="btn btn-success button-setting rounded-0 pt-0 pb-0 pr-4 pl-4">Edytuj
                            dane</a>
                    </div>
                    <div style="width: 10px"></div>
                    <div>
                        <a href="/app/user/change-pass"
                           class="btn btn-success button-setting rounded-0 pt-0 pb-0 pr-4 pl-4">Zmień hasło</a>
                    </div>
                    <div style="width: 10px"></div>
                    <div>
                        <a href="/app/user/add-user-setting" class="btn btn-success rounded-0 pt-0 pb-0 pr-4 pl-4">Dodaj
                            konto</a>
                    </div>
                </div>

                <div class="schedules-content">
                    <table class="table border-bottom">
                        <thead>
                        <tr class="d-flex">
                            <th>Lp</th>
                            <th class="col-2">binanceKey</th>
                            <th class="col-2">binanceSecret</th>
                            <th class="col-2">Active</th>
                            <th class="col-2">Signal</th>
                            <th class="col-1">Max zleceń</th>
                            <th class="col-2 center">AKCJE</th>
                        </tr>
                        </thead>
                        <tbody class="text-color-lighter">
                        <c:forEach var="setting" items="${user.userSetting}" varStatus="stat">
                            <tr class="d-flex">
                                <td><c:out value="${stat.index + 1}"/></td>
                                <td class="col-2 key-column"><c:out value="${setting.binanceKey}"/></td>
                                <td class="col-2 secret-column"><c:out value="${setting.binanceSecret}"/></td>
                                <td class="col-2"><c:out value="${setting.active}"/></td>
                                <td class="col-2"><c:out value="${setting.activeSignal}"/></td>
                                <td class="col-1"><c:out value="${setting.maxCountOrder}"/></td>
                                <td class="col-2 d-flex align-items-center justify-content-center flex-wrap">
                                    <a href="/app/user/delete-user-setting?settingId=${setting.getId()}"
                                       class="btn btn-danger rounded-0 text-light m-1">Usuń</a>
                                    <a href="/app/user/edit-user-setting?settingId=${setting.getId()}"
                                       class="btn btn-info rounded-0 text-light m-1">Edytuj</a>
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