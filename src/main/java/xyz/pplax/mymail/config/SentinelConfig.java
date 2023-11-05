package xyz.pplax.mymail.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class SentinelConfig {

    @Bean
    public void initFlowRules() {
        FlowRule rule = new FlowRule();
        rule.setResource("my-mail-api-resource"); // 资源名，对应@SentinelResource注解中的value属性
        rule.setCount(10); // 限制的请求数量
        rule.setGrade(RuleConstant.FLOW_GRADE_QPS); // 限流的阈值类型，这里设置为QPS
        rule.setLimitApp("default"); // 限制来源，default表示不区分来源

        FlowRuleManager.loadRules(Collections.singletonList(rule));
    }
}