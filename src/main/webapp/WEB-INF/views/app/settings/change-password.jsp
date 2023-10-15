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
                <div class="container">
                    <div class="row" style="margin-top: 40px">
                        <div class="col-1"></div>
                        <div class="col-10" style="padding-bottom: 20px"><h2>Zmiana hasła</h2></div>
                        <div class="col-1"></div>
                    </div>
                    <div class="row">
                        <div class="col-1"></div>
                        <div class="col-6">
                            <form:form method="post" modelAttribute="newPassword">
                                <div class="form-group">
                                    <label for="pass1">Nowe hasło</label>
                                    <form:password path="pass1" class="form-control"/><form:errors path="pass1"
                                                                                                   cssClass="error"/>
                                </div>
                                <div class="form-group">
                                    <label for="pass2">Powtórz hasło</label>
                                    <form:password path="pass2" class="form-control"/><form:errors path="pass2"
                                                                                                   cssClass="error"/>
                                </div>
                                <div class="form-group">
                                    <label for="pass2">Token wysłany w wiadomości email</label>
                                    <form:input path="token" class="form-control"/><form:errors path="token"
                                                                                                cssClass="error"/>
                                </div>
                                <form:button class="btn btn-primary" type="submit">Zmień hasło</form:button>
                                <form:button class="btn btn-secondary" type="reset">Wyczyść dane</form:button>
                                <sec:csrfInput/>
                            </form:form>
                        </div>
                        <div class="col-5"></div>
                    </div>
                </div>
            </div>
        </div>

    </div>
</section>

</body>

</html>
<%@include file="../../footer.jsp" %>