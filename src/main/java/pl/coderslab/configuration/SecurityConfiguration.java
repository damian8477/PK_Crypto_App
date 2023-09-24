package pl.coderslab.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import javax.servlet.http.Cookie;
import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final DataSource dataSource;

    @Autowired
    public SecurityConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder())
                .usersByUsernameQuery("SELECT username, password, active FROM users WHERE username = ?")
                .authoritiesByUsernameQuery("SELECT username, role FROM users WHERE username = ?");
    }

    @Bean
    public LogoutHandler logoutHandler() {
        return (request, response, authentication) -> {
            // Usuń ciasteczko JSESSIONID ręcznie
            Cookie cookie = new Cookie("JSESSIONID", "");
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/login").permitAll() // Ścieżka dostępna publicznie
                    .antMatchers("/admin/*").hasRole("ADMIN")
                    .antMatchers("/starter").hasAnyRole("ADMIN", "USER")
                    .and()
                .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/starter")// Po zalogowaniu przekieruj na /private
                    .permitAll()
                .and()
                    .logout()
                    .permitAll()
                    .logoutUrl("/logout") // Ścieżka wylogowania
                    .logoutSuccessUrl("/login") // Przekierowanie po wylogowaniu
                    .invalidateHttpSession(true) // Zakończ sesję użytkownika
                    .deleteCookies("JSESSIONID"); // Usuń ciasteczko sesji
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .logout()
//                    .logoutUrl("/logout")
//                    .logoutSuccessUrl("/login?logout")
//                    .invalidateHttpSession(true)
//                    .addLogoutHandler(logoutHandler())
//                    .deleteCookies("JSESSIONID")
//                    .permitAll()
//                    .and()
//                .authorizeRequests()
//                    .antMatchers("/starter").authenticated() // Strona dostępna tylko dla zalogowanych
//                    .anyRequest().permitAll() // Pozwól na dostęp do innych stron
//                    .and()
//                .formLogin()
//                    .loginPage("/login") // Strona logowania
//                    .defaultSuccessUrl("/starter")
//                    .permitAll();
//
////        http
////                // ... Inne konfiguracje Spring Security ...
////                .logout()
////                .logoutUrl("/logout") // Ścieżka wylogowania
////                .logoutSuccessUrl("/login?logout") // Przekierowanie po wylogowaniu
////                .invalidateHttpSession(true) // Zakończ sesję użytkownika
////                .deleteCookies("JSESSIONID") // Usuń ciasteczko sesji
////                .and()
////
////                .authorizeRequests()
////                .antMatchers("/starter").authenticated() // Strona dostępna tylko dla zalogowanych
//////                    .anyRequest().permitAll() // Pozwól na dostęp do innych stron
//////
////                .antMatchers("/logout").permitAll();
////        http
////                .authorizeRequests()
////                    .antMatchers("/login").permitAll()   // Dodajemy stronę główną, aby mógł wejść na nią każdy
////                    .antMatchers("/register").anonymous()
////                    .antMatchers("/starter").authenticated()
////                    .anyRequest().authenticated()
////                    .and()
////                .formLogin()
////                    .loginPage("/login")
////                    .defaultSuccessUrl("/starter") // Usuwamy plik `index.html` i dajemy ścieżkę do kontrolera strony głównej
////                    .and()
////                .logout()
////                    .logoutSuccessUrl("/login");  // j.w.
////        http.authorizeRequests()
////                    .antMatchers("/register").permitAll()
////                    .antMatchers("/starter").authenticated()
////                    .antMatchers("/login").anonymous()
////                    .anyRequest().authenticated()
////                .and()
////                    .formLogin()
////                    .loginPage("/login")
////                    .defaultSuccessUrl("/starter")
////                .and()
////                    .logout()
////                    .logoutUrl("/logout")
////                    .logoutSuccessUrl("/login");
//    }
}
