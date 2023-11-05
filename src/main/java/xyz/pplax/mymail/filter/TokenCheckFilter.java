package xyz.pplax.mymail.filter;

import org.springframework.stereotype.Component;
import xyz.pplax.mymail.model.entity.User;
import xyz.pplax.mymail.service.UserService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用于检擦用户登录状态
 */
@Component
public class TokenCheckFilter implements Filter {

    private final UserService userService;

    public TokenCheckFilter(UserService userService) {
        this.userService = userService;
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化过滤器
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // 在请求被处理之前进行过滤操作
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // 检查token
        String token = httpRequest.getHeader("token");
        if (token == null) {
            // 没有token，登录状态有问题
            RequestDispatcher dispatcher = request.getRequestDispatcher("/err/paramErr");
            dispatcher.forward(request, response);
        } else {
            User user = userService.selectByToken(token);
            if (user == null) {
                // 执登录状态无效或过期
                RequestDispatcher dispatcher = request.getRequestDispatcher("/err/expired");
                dispatcher.forward(request, response);
            } else {
                chain.doFilter(request, response);
            }
        }
    }

    @Override
    public void destroy() {
        // 销毁过滤器
    }
}
