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
        <div class="m-4 p-3 width-medium text-color-darker">
            <div class="dashboard-content border-dashed p-3 m-4 view-height">
                <div class="mt-4 ml-4 mr-4">
                    <form:form method="post" modelAttribute="strategy">
                    <div class="schedules-content">
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Źródło</label>
                            <div class="col-sm-10">
                                <form:select id="source" path="source"
                                             cssClass="col-sm-10 label-size col-form-label">
                                    <form:options items="${sourceList}" itemLabel="name"/><form:errors
                                        path="source"/>
                                </form:select>
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Name</label>
                            <div class="col-sm-10">
                                <form:input path="name" class="form-control"/><form:errors path="name"/>
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Ilość $ lub % w zależności od
                                wyboru</label>
                            <div class="col-sm-10">
                                <form:input path="percentOfMoney" class="form-control"/><form:errors
                                    path="percentOfMoney"/>
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Czy procentowa wartość zlecenia</label>
                            <div class="col-sm-10">
                                <form:checkbox path="percentMoney" class="form-control"/><form:errors
                                    path="percentMoney"/>
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Aktywne</label>
                            <div class="col-sm-10">
                                <form:checkbox path="active" class="form-control"/>
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Dźwignia</label>
                            <div class="col-sm-10">
                                <form:input path="maxLeverage" class="form-control"/><form:errors path="maxLeverage"/>
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Maksymalna liczba zleceń</label>
                            <div class="col-sm-10">
                                <form:input path="maxCountOrders" class="form-control"/><form:errors path="maxCountOrders"/>
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">MarginType</label>
                            <div class="col-sm-10">
                                <form:select id="marginType" path="marginType"
                                             cssClass="col-sm-10 label-size col-form-label">
                                    <form:options items="${marginTypes}" itemLabel="name"/><form:errors
                                        path="marginType"/>
                                </form:select>
                            </div>
                        </div>
                        <form:hidden path="id"/><form:errors path="id"/><br>
                        <form:button class="btn btn-color rounded-0 pt-0 pb-0 pr-4 pl-4">Zapisz</form:button>
                        </form:form>
                    </div>
                </div>
            </div>
        </div>
</section>

</body>

</html>
<%@include file="../../footer.jsp" %>
