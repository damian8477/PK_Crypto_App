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
                <form:form method="post" modelAttribute="order">
                <div class="schedules-content">
                    <div class="form-group row">
                        <label class="col-sm-2 label-size col-form-label">${order.symbolName}</label>
                    </div>
                    <br>
                    <div class="form-group row">
                        <label class="col-sm-2 label-size col-form-label">Cena wejścia: ${order.entry}</label>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-2 label-size col-form-label">Cena aktualna: ${marketPrice}</label>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-2 label-size col-form-label">Lot</label>
                        <div class="col-sm-10">
                            <form:input path="lot" class="form-control"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-2 label-size col-form-label" >Niezrealizowany PLN:
                            <c:if test="${order.profitProcent >= 0}">
                                <p style="color: green">${order.profitProcent}</p>
                            </c:if>
                            <c:if test="${order.profitProcent < 0}">
                                <p style="color: red">${order.profitProcent}</p>
                            </c:if>
                        </label>
                    </div>
                    <form:hidden path="appOrder"/>
                    <form:hidden path="side"/>
                    <form:hidden path="amount"/>
                    <form:hidden path="leverage"/>
                    <form:hidden path="id"/>
                    <form:hidden path="symbolName"/>
                    <form:button class="btn btn-color rounded-0 pt-0 pb-0 pr-4 pl-4">Zamknij</form:button>
                    </form:form>
                </div>
                <br><br>

            </div>
        </div>
</section>

</body>

</html>
<%@include file="../../../footer.jsp" %>