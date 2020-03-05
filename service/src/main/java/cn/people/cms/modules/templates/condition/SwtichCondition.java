package cn.people.cms.modules.templates.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by lml on 2018/2/28.
 * 根据模板的开关配置决定定时任务的启动与否
 */
public class SwtichCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return Boolean.valueOf(conditionContext.getEnvironment().getProperty("theone.freemarker.switch")).equals(true);
    }
}
