package xyz.pplax.mymail.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.pplax.mymail.filter.TokenCheckFilter;
import xyz.pplax.mymail.service.UserService;

@Configuration
public class FilterConfig {

    @Autowired
    UserService userService;

    @Bean
    public FilterRegistrationBean<TokenCheckFilter> userFilterFilterRegistrationBean() {
        FilterRegistrationBean<TokenCheckFilter> registration = new FilterRegistrationBean<TokenCheckFilter>();
        registration.setFilter(new TokenCheckFilter(userService));

        // 指定需要过滤的URL
        registration.addUrlPatterns("/api/User/*");
        registration.addUrlPatterns("/api/Article/*");
        registration.addUrlPatterns("/api/Role/*");

        return registration;
    }
}
