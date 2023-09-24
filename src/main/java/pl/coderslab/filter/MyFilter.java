package pl.coderslab.filter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class MyFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String userName = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().findFirst().orElse(null).toString();
        System.out.println(userName);
        request.setAttribute("userRole", userName);
        System.out.println("Filtr działa!");
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicjalizacja filtru (opcjonalna)
    }

    @Override
    public void destroy() {
        // Zwalnianie zasobów filtru (opcjonalne)
    }
}