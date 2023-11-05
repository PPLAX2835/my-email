package xyz.pplax.mymail.component.handler;


import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import xyz.pplax.mymail.model.resp.ResponseCode;
import xyz.pplax.mymail.model.resp.ResponseResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义sentinel异常返回信息
 */
@Component
public class CommonBlockHandler implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws Exception {

        String msg = null;
        int code = 500;
        if (ex instanceof FlowException) {
            msg = "请求被限流了";
            code = 429; // 429表示请求过多
        } else if (ex instanceof DegradeException) {
            msg = "降级了";
        } else if (ex instanceof ParamFlowException) {
            msg = "热点参数限流";
        } else if (ex instanceof SystemBlockException) {
            msg = "系统规则（负载/...不满足要求）";
        } else if (ex instanceof AuthorityException) {
            msg = "授权规则不通过";
        }
        // http状态码
        response.setStatus(code);
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        response.setContentType("application/json;charset=utf-8");
        // spring mvc自带的json操作工具，叫jackson
        new ObjectMapper()
                .writeValue(
                        response.getWriter(),
                        ResponseResult.error(ResponseCode.ERROR, msg)
                );
    }
}


