<header class="page-header">
  <nav class="navbar navbar-expand-lg justify-content-between">
    <a href="/" class="navbar-brand main-logo">
      Crypto
    </a>
    <div class="d-flex justify-content-around">
      <h4 class="text-light mr-3">${username}</h4>
      <div class="circle-div text-center"><i class="fas fa-user icon-user"></i></div>

    </div>
    <div class="d-flex justify-content-around">
        <form method="post" action="/logout">
          <button type="submit" class="nav-link color-header">Wyloguj</button>
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        </form>
    </div>

  </nav>
</header>


