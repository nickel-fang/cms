package cn.people.cms.modules.analysis.service.impl;

import cn.people.api.RpcIUserService;
import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.modules.analysis.model.Editors;
import cn.people.cms.modules.analysis.service.IEditorService;
import cn.people.cms.modules.cms.model.Article;
import cn.people.cms.modules.user.model.User;
import cn.people.cms.modules.user.service.impl.UserService;
import cn.people.domain.IUser;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 人员统计service
 *
 * Created by cuiyukun on 2017/7/3.
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
@Slf4j
public class EditorService extends BaseService<Editors> implements IEditorService {

    @Autowired
    private UserService userService;

    @Override
    public QueryResult listPage(Integer pageNumber, Integer pageSize, String startTime, String endTime, String type) {
        Sql sql = Sqls.create("select name, sum($type) as $type from analysis_editor where create_date >= @startTime && create_date <= @endTime group by name order by sum($type) desc");
        sql.vars().set("type", type);
        sql.params().set("startTime", startTime);
        sql.params().set("endTime", endTime);
        return super.listPage(pageNumber, pageSize, sql);
    }

    /**
     * 构造一个定时器，定时往analysis_editor表里存入数据
     * 每天凌晨00:05查询并插入前一天数据
     */
    @Scheduled(cron = "0 5 0 * * ?")
    @Transactional
    public void insertManuscript() {
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println("当前时间为:" + localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "，文章数据存入了analysis_editor表中");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar formerDay = Calendar.getInstance();
        Calendar theDay = Calendar.getInstance();
        Calendar latterDay = Calendar.getInstance();
        Date date = new Date();
        formerDay.setTime(date);
        theDay.setTime(date);
        latterDay.setTime(date);
        formerDay.set(Calendar.HOUR, -24);
        Date formerDate = formerDay.getTime();
        Date latterDate = latterDay.getTime();
        //前天
        String former = dateFormat.format(formerDate);
        //当天
        String latter = dateFormat.format(latterDate);

        List<User> list = userService.findAll();
        Iterator<User> iterator = list.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            Integer articles = dao.count(Article.class, Cnd.where("publish_date", ">", former).and("publish_date", "<", latter).and("create_by", "=", user.getId()).and("del_flag", "!=", 3));
            Integer hits = dao.func(Article.class, "sum", "hits", Cnd.where("publish_date", ">", former).and("publish_date", "<", latter).and("create_by", "=", user.getId()).and("del_flag", "!=", 3));
            Integer comments = dao.func(Article.class, "sum", "comments", Cnd.where("publish_date", ">", former).and("publish_date", "<", latter).and("create_by", "=", user.getId()).and("del_flag", "!=", 3));
            Editors editors = new Editors();
            editors.setName(user.getName());
            editors.setArticles(articles);
            editors.setHits(hits);
            editors.setComments(comments);
            editors.setCreateAt(formerDate);
            dao.insert(editors);
        }
    }

}
