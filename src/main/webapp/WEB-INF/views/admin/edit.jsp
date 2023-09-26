<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%@include file="../head.jsp" %>
<%@include file="../header.jsp" %>
<body>
<section class="dashboard-section">
    <div class="row dashboard-nowrap">
        <%@include file="../app/Dashboard.jsp" %>
        <div class="m-4 p-3 width-medium text-color-darker">
            <div class="dashboard-content border-dashed p-3 m-4 view-height">
                <div class="mt-4 ml-4 mr-4">
                    <form:form method="post" action="/admin/edit" modelAttribute="user">
                        <div class="row border-bottom border-3">
                            <div class="col"><h3 class="color-header text-uppercase">Edycja użytkownika</h3></div>
                            <div class="col d-flex justify-content-end mb-2"><button type="submit" class="btn btn-color rounded-0 pt-0 pb-0 pr-4 pl-4">Zapisz</button></div>
                        </div>

                        <table class="table borderless">
                            <tbody>
                            <tr class="d-flex">
                                <th scope="row" class="col-2">Username</th>
                                <td class="col-7">
                                    <form:input class="w-100 p-1" path="username"/>
                                </td>
                            </tr>
                            <tr class="d-flex">
                                <th scope="row" class="col-2">Imię</th>
                                <td class="col-7">
                                    <form:input class="w-100 p-1" path="firstName"/>
                                </td>
                            </tr>
                            <tr class="d-flex">
                                <th scope="row" class="col-2">Nazwa Przepisu</th>
                                <td class="col-7">
                                    <form:input class="w-100 p-1" path="lastName"/>
                                </td>
                            </tr>
                                    <form:hidden path="password"/>
                                    <form:hidden path="active"/>
                                    <form:hidden path="role"/>
                                    <form:hidden path="id"/>
<%--                                    <form:hidden path="strategies"/>--%>
<%--                                    <form:hidden path="orders"/>--%>

<%--                            <tr class="d-flex">--%>
<%--                                <th scope="row" class="col-2">Opis przepisu</th>--%>
<%--                                <td class="col-7"> <textarea class="w-100 p-1" rows="5" name="description">${recipe.description}</textarea></td>--%>
<%--                            </tr>--%>
<%--                            <tr class="d-flex">--%>
<%--                                <th scope="row" class="col-2">Przygotowanie (minuty)</th>--%>
<%--                                <td class="col-3">--%>
<%--                                    <form:input class="p-1" type="number" path="firstName" name="preparationTime"/>--%>
<%--                                </td>--%>
<%--                            </tr>--%>
                            </tbody>
                        </table>
                    </form:form>
                </div>

            </div>
        </div>
    </div>
</section>

</body>
</html>
<%@include file="../footer.jsp" %>

