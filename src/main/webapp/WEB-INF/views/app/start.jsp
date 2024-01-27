<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%@include file="../head.jsp" %>
<%@include file="../header.jsp" %>
<body>
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<section class="dashboard-section">
    <div class="row dashboard-nowrap">
        <%@include file="dashboard.jsp" %>

        <div class="m-4 p-3 width-medium">
            <div class="dashboard-content border-dashed p-3 m-4 view-height">
                <div class="row border-bottom border-3 p-1 m-1">
                    <div class="col noPadding">
                        <h3 class="color-header text-uppercase">Witaj</h3>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
</body>

</html>
<%@include file="../footer.jsp" %>