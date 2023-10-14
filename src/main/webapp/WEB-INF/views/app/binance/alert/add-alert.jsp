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
                <label class="col-sm-2 label-size col-form-label">${alert.symbol}</label>
                <label class="col-sm-2 label-size col-form-label">${alert.marketPrice}</label>

                <form:form method="post" modelAttribute="alert">
                <div class="schedules-content">
                    <form:hidden path="symbol"/>
                    <form:hidden path="marketPrice"/>
                    <div class="alert-div">
                        <label class="label-size col-form-label">Alert 1</label>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Wybór strony:</label>
                            <div class="col-sm-10">
                                <form:select id="positionSide" path="positionSide1" cssClass="col-sm-10 label-size col-form-label">
                                    <form:options items="${positionSides}" itemLabel="name"/><form:errors path="positionSide1"/>
                                </form:select>
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Cena wejścia</label>
                            <div class="col-sm-10">
                                <form:input path="alertPrice1"/><form:errors path="alertPrice1"/>
                            </div>
                        </div>
                    </div>
                    <br><br>
                    <div class="alert-div">
                        <label class="label-size col-form-label">Alert 2</label>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Wybór strony:</label>
                            <div class="col-sm-10">
                                <form:select id="positionSide" path="positionSide2" cssClass="col-sm-10 label-size col-form-label">
                                    <form:options items="${positionSides}" itemLabel="name"/><form:errors path="positionSide1"/>
                                </form:select>
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Cena wejścia</label>
                            <div class="col-sm-10">
                                <form:input path="alertPrice2"/><form:errors path="alertPrice1"/>
                            </div>
                        </div>
                    </div>
                    <br><br>
                    <div class="alert-div">
                        <label class="label-size col-form-label">Alert 3</label>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Wybór strony:</label>
                            <div class="col-sm-10">
                                <form:select id="positionSide" path="positionSide3" cssClass="col-sm-10 label-size col-form-label">
                                    <form:options items="${positionSides}" itemLabel="name"/><form:errors path="positionSide1"/>
                                </form:select>
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Cena wejścia</label>
                            <div class="col-sm-10">
                                <form:input path="alertPrice3"/><form:errors path="alertPrice1"/>
                            </div>
                        </div>
                        <br><br>
                    </div>

                    <div class="alert-div">
                        <label class="label-size col-form-label">Alert 4</label>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Wybór strony:</label>
                            <div class="col-sm-10">
                                <form:select id="positionSide" path="positionSide4" cssClass="col-sm-10 label-size col-form-label">
                                    <form:options items="${positionSides}" itemLabel="name"/><form:errors path="positionSide1"/>
                                </form:select>
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Cena wejścia</label>
                            <div class="col-sm-10">
                                <form:input path="alertPrice4"/><form:errors path="alertPrice1"/>
                            </div>
                        </div>
                    </div>
                    <br><br>
                    <div class="alert-div">
                        <label class="label-size col-form-label">Alert 5</label>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Wybór strony:</label>
                            <div class="col-sm-10">
                                <form:select id="positionSide" path="positionSide5" cssClass="col-sm-10 label-size col-form-label">
                                    <form:options items="${positionSides}" itemLabel="name"/><form:errors path="positionSide1"/>
                                </form:select>
                            </div>
                        </div>
                        <div class="form-group row">
                            <label class="col-sm-2 label-size col-form-label">Cena wejścia</label>
                            <div class="col-sm-10">
                                <form:input path="alertPrice5"/><form:errors path="alertPrice1"/>
                            </div>
                        </div>
                    </div>
                    <br><br>
                    <form:button class="btn btn-color rounded-0 pt-0 pb-0 pr-4 pl-4">Dodaj</form:button>
                    </form:form>
                </div>
            </div>
        </div>
</section>

</body>

</html>
<%@include file="../../../footer.jsp"%>