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
                    <label class="col-sm-2 label-size col-form-label">${ownSignal.symbol}</label>


                <form:form method="post" modelAttribute="ownSignal">
                <div class="schedules-content">
                    <form:hidden path="symbol"/>
                    <div class="form-group row">
                        <label class="col-sm-2 label-size col-form-label">Wybór strony:</label>
                        <div class="col-sm-10">
                            <form:select id="positionSide" path="positionSide" cssClass="col-sm-10 label-size col-form-label">
                                <form:options items="${positionSides}" itemLabel="name"/>
                            </form:select>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-2 label-size col-form-label">Strategia</label>
                        <div class="col-sm-10">
                            <form:select id="strategySetting" path="strategySetting" cssClass="col-sm-10 label-size col-form-label">
                                <form:options items="${user.strategies}" itemLabel="name"/>
                            </form:select>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-2 label-size col-form-label">Typ zlecenia</label>
                        <div class="col-sm-10">
                            <form:select id="typeOrder" path="typeOrder" cssClass="col-sm-10 label-size col-form-label">
                                <form:options items="${orderTypes}" itemLabel="name"/>
                            </form:select>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-2 label-size col-form-label">Dźwignia</label>
                        <div class="col-sm-10">
                            <form:input path="lever"/><form:errors path="lever"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-2 label-size col-form-label">Cena wejścia</label>
                        <div class="col-sm-10">
                            <form:input path="entryPrice"/><form:errors path="entryPrice"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-2 label-size col-form-label">% Take profit</label>
                        <div class="col-sm-10">
                            <form:checkbox path="tpPercent"/>
                        </div>
                        <label class="col-sm-2 label-size col-form-label">TakeProfit</label>
                        <div class="col-sm-10">
                            <form:input path="takeProfit"/><form:errors path="takeProfit"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-2 label-size col-form-label">% Stop loss</label>
                        <div class="col-sm-10">
                            <form:checkbox path="slPercent"/>
                        </div>
                        <label class="col-sm-2 label-size col-form-label">StopLoss</label>
                        <div class="col-sm-10">
                            <form:input path="stopLoss"/><form:errors path="stopLoss"/>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-2 label-size col-form-label">Wybrana jednostka</label>
                        <div class="col-sm-10">
                            <form:select id="cashType" path="cashType" cssClass="col-sm-10 label-size col-form-label">
                                <form:options items="${cashTypes}" itemLabel="name"/>
                            </form:select>
                        </div>
                        <label class="col-sm-2 label-size col-form-label">Lot</label>
                        <div class="col-sm-10">
                            <form:input path="lot"/><form:errors path="lot"/>
                        </div>
                        <label class="col-sm-2 label-size col-form-label">$</label>
                        <div class="col-sm-10">
                            <form:input path="amount"/><form:errors path="amount"/>
                        </div>
                        <label class="col-sm-2 label-size col-form-label">% konta</label>
                        <div class="col-sm-10">
                            <form:input path="percentOfAccount"/><form:errors path="percentOfAccount"/>
                        </div>
                    </div>
                    <form:button class="btn btn-color rounded-0 pt-0 pb-0 pr-4 pl-4">Dodaj</form:button>
                    </form:form>
                </div>
            </div>
        </div>
</section>

</body>

</html>
<%@include file="../../../footer.jsp"%>