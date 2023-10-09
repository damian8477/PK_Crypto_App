package pl.coderslab.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.coderslab.filter.MyFilter;

@Configuration
public class FilterConfig {
//    @Bean
//    public FilterRegistrationBean<MyFilter> myFilter() {
//        FilterRegistrationBean<MyFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new MyFilter());
//        registrationBean.addUrlPatterns("/*"); // Wszystkie ścieżki
//        return registrationBean;
//    }
}