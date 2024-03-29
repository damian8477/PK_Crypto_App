<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%@include file="../../head.jsp" %>
<%@include file="../../header.jsp" %>
<body>
<section class="dashboard-section">
    <div class="row dashboard-nowrap">
        <%@include file="../dashboard.jsp" %>
        <div class="m-4 p-3 width-medium text-color-darker">
            <div class="dashboard-content border-dashed p-3 m-4 view-height">
                <div class="mt-4 ml-4 mr-4">
                    <form:form method="post" modelAttribute="source">
                    <div class="schedules-content">
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Name</label>
                            <div class="col-sm-10">
                                <form:input path="name" class="form-control"/><form:errors path="name"/>
                            </div>
                        </div>

                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Description</label>
                            <div class="col-sm-10">
                                <form:textarea path="description" class="form-control"/><form:errors
                                    path="description"/>
                            </div>
                        </div>

                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Procent po którym ustawić BE</label>
                            <div class="col-sm-10">
                                <form:input path="bePercent" class="form-control"/><form:errors path="bePercent"/>
                            </div>
                        </div>

                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Aktywne kroczący stop loss</label>
                            <div class="col-sm-10">
                                <form:checkbox path="walkingStopLoss" class="form-control"/><form:errors
                                    path="walkingStopLoss"/>
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Aktywne domyślny take profit</label>
                            <div class="col-sm-10">
                                <form:input path="basicTpPercent" class="form-control"/><form:errors
                                    path="basicTpPercent"/>
                                <form:checkbox path="activeBasicTp" class="form-control"/>
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Aktywny domyślny stop loss</label>
                            <div class="col-sm-10">
                                <form:input path="basicSlPercent" class="form-control"/><form:errors
                                    path="basicSlPercent"/>
                                <form:checkbox path="activeBasicSl" class="form-control"/>
                            </div>
                        </div>
                        <form:hidden path="id"/><form:errors path="id"/><br>
                        <form:hidden path="symbols"/>
                            <%--                    <form:hidden path="strategies"/><form:errors path="strategies"/><br>--%>
                        <form:hidden path="adminStrategy"/><form:errors path="adminStrategy"/><br>
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

