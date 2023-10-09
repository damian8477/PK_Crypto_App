<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<ul class="nav flex-column long-bg">
    <li class="nav-item">
        <a class="nav-link" href="/app/orders/list">
            <span>Aktywne zlecenia</span>
            <i class="fas fa-angle-right"></i>
        </a>
    </li>
    <li class="nav-item">
        <a class="nav-link" href="/app/history-orders/list">
            <span>Historia zleceń</span>
            <i class="fas fa-angle-right"></i>
        </a>
    </li>
    <li class="nav-item">
        <a class="nav-link" href="/app/binance/symbol-list">
            <span>Kryptowaluty</span>
            <i class="fas fa-angle-right"></i>
        </a>
    </li>
    <li class="nav-item">
        <a class="nav-link" href="/app/strategy/list">
            <span>Strategie</span>
            <i class="fas fa-angle-right"></i>
        </a>
    </li>
    <sec:authorize access="hasRole('ROLE_ADMIN')">
        <li class="nav-item">
            <a class="nav-link" href="/admin/user-list">
                <span>Użytkownicy</span>
                <i class="fas fa-angle-right"></i>
            </a>
        </li>
    </sec:authorize>
    <li class="nav-item">
        <a class="nav-link" href="/app/user/settings">
            <span>Ustawienia</span>
            <i class="fas fa-angle-right"></i>
        </a>
    </li>
</ul>