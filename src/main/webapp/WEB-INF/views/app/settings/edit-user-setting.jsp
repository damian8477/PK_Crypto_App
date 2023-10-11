<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%@include file="../../head.jsp" %>
<%@include file="../../header.jsp" %>
<body>
<section class="dashboard-section">
    <div class="row dashboard-nowrap">
        <%@include file="../dashboard.jsp" %>

        <div class="m-4 p-3 width-medium">
            <div class="dashboard-content border-dashed p-3 m-4 view-height">


                <form:form method="post" modelAttribute="userSetting">
                <div class="schedules-content">
                    <div class="form-group row">
                        <label class="col-sm-2 label-size col-form-label">Binance Key</label>
                        <div class="col-sm-10">
                            <form:input path="binanceKey" class="form-control"/><form:errors path="binanceKey"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-2 label-size col-form-label">Binance Secret</label>
                        <div class="col-sm-10">
                            <form:input path="binanceSecret" class="form-control"/><form:errors path="binanceSecret"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-2 label-size col-form-label">Aktywny:</label>
                        <div class="col-sm-10">
                            <form:checkbox path="active" class="form-control"/>
                        </div>
                    </div>

                    <div class="form-group row">
                        <label class="col-sm-2 label-size col-form-label">Aktywne sygnaly</label>
                        <div class="col-sm-10">
                            <form:checkbox path="activeSignal" class="form-control"/>
                        </div>
                    </div>
                    <form:hidden path="id"/>
                    <form:button class="btn btn-color rounded-0 pt-0 pb-0 pr-4 pl-4">Dodaj</form:button>
                    </form:form>
                </div>
<%--                <form method="get" action="app/user/edit-user-setting/generate">--%>
<%--                    <div class="form-group row">--%>
<%--                        <label class="col-sm-2 label-size col-form-label">Telegram aktywacja</label>--%>
<%--&lt;%&ndash;                        <c:if test="${isGenerate == true}">&ndash;%&gt;--%>
<%--                            <div class="col-sm-10">--%>
<%--                                <label class="col-sm-2 label-size col-form-label">${generatedCode}</label>--%>
<%--                            </div>--%>
<%--&lt;%&ndash;                        </c:if>&ndash;%&gt;--%>
<%--                        <form hidden="hidden"  "${userSetting}"/>--%>
<%--                        <form class="btn btn-color rounded-0 pt-0 pb-0 pr-4 pl-4">Generuj kod</form>--%>
<%--                    </div>--%>

<%--                </form>--%>
                    <br><br>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Telegram<br>Kod aktywacyjny<br>(ważny 15 min lub 3 próby autoryzacji)</label>

                            <div class="col-sm-10">
                                <label class="col-sm-2 label-size col-form-label">${generatedCode} </label>
                            </div>
                        </div>

            </div>
        </div>
</section>

</body>

</html>
<%@include file="../../footer.jsp" %>