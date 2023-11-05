package xyz.pplax.mymail.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.pplax.mymail.component.filter.TokenCheckFilter;
import xyz.pplax.mymail.service.UserService;

@Configuration
public class FilterConfig {

    @Autowired
    UserService userService;

    @Bean
    public FilterRegistrationBean<TokenCheckFilter> userFilterFilterRegistrationBean() {
        FilterRegistrationBean<TokenCheckFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(new TokenCheckFilter(userService));

        // 指定需要过滤的URL
        filterFilterRegistrationBean.addUrlPatterns("/api/email/*");
        filterFilterRegistrationBean.addUrlPatterns("/api/menus/*");
        filterFilterRegistrationBean.addUrlPatterns("/api/user/*");

        return filterFilterRegistrationBean;
    }
}
