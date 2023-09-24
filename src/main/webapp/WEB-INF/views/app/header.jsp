<header class="page-header">
  <nav class="navbar navbar-expand-lg justify-content-between">
    <a href="/" class="navbar-brand main-logo">
      Crypto
    </a>
    <div class="d-flex justify-content-around">
      <h4 class="text-light mr-3">${user.firstName}</h4>
      <div class="circle-div text-center"><i class="fas fa-user icon-user"></i></div>
      <li class="nav-item ml-4">
        <a class="nav-link color-header" href="/logout">logout</a>
        <form action="/logout" method="post">
          <input type="submit" value="Wyloguj się" />
        </form>
      </li>

      <sec:authorize access="isAuthenticated()">
        <div style="margin-right: 20px"> Witaj, <strong>${pageContext.request.userPrincipal.principal.username}</strong></div>
        <form class="form-inline mt-3" method="post" action="/logout">
          <button class="btn btn-outline-primary" type="submit">Wyloguj</button>
          <sec:csrfInput/>
        </form>
      </sec:authorize>
    </div>
  </nav>
</header>


