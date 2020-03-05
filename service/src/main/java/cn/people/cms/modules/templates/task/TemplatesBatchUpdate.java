package cn.people.cms.modules.templates.task;


import cn.people.cms.base.dao.BaseDao;
import cn.people.cms.modules.cms.service.ISiteService;
import cn.people.cms.modules.templates.condition.SwtichCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by lml on 2018/2/28.
 */
@Slf4j
@Component
@Conditional(SwtichCondition.class)
public class TemplatesBatchUpdate {

    @Lazy
    @Autowired
    private ISiteService siteService;
    @Autowired
    private BaseDao dao;

    /**
     * 每天定时更新所有文章、页面对应的模板数据
     */
    @Scheduled(cron="0 0 2  * * ? ")
    private void update(){
        log.info("-------templates batch update-------");
        log.info("------data  update end---------");
    }
}
