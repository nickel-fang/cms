package cn.people.cms.modules.analysis.service.impl;

import cn.people.api.RpcIDictService;
import cn.people.cms.base.dao.BaseDao;
import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.modules.analysis.model.Manuscript;
import cn.people.cms.modules.analysis.service.IManuscriptService;
import cn.people.cms.modules.cms.model.Article;
import cn.people.cms.modules.cms.service.IArticleService;
import cn.people.cms.modules.sys.model.Dict;
import cn.people.cms.modules.sys.service.IDictService;
import cn.people.domain.IDict;
import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.RoseType;
import com.github.abel533.echarts.code.Symbol;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.code.X;
import com.github.abel533.echarts.data.PieData;
import com.github.abel533.echarts.series.Line;
import com.github.abel533.echarts.series.Pie;
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
import java.util.*;

/**
 * 稿件统计service
 * @author  cuiyukun
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
@Slf4j
public class ManuscriptService extends BaseService<Manuscript> implements IManuscriptService {

    @Autowired
    private BaseDao dao;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private IDictService dictService;

    @Override
    public Option getScriptLine(String type, String startTime, String endTime, String size) {
        if (!(type.equals("articles")||type.equals("hits")||type.equals("comments")||type.equals("likes"))){
            return null;
        }
        //获取数据
        List<Manuscript> list = getData(type, startTime, endTime, size);
        Object[] data = new Object[list.size()];

        //折线图
        Option option = new Option();
        option.color("#32cd32");
        option.tooltip().trigger(Trigger.axis);
        option.toolbox().show(true);
        option.calculable(true);
        Iterator<Manuscript> iteratorDate = list.iterator();
        int len = 0;
        switch (size) {
            case "day":
                while (iteratorDate.hasNext()) {
                    Manuscript manuscript = iteratorDate.next();
                    data[len] = stampToDate(manuscript.getCreateAt());
                    len ++;
                }
                option.xAxis(new CategoryAxis().boundaryGap(false).data(data));
                break;
            case "week":
                while (iteratorDate.hasNext()) {
                    Manuscript manuscript = iteratorDate.next();
                    data[len] = stampToDate(manuscript.getCreateAt());
                    len ++;
                }
                option.xAxis(new CategoryAxis().boundaryGap(false).data(data));
                break;
            case "month":
                while (iteratorDate.hasNext()) {
                    Manuscript manuscript = iteratorDate.next();
                    data[len] = stampToDate(manuscript.getCreateAt());
                    len ++;
                }
                option.xAxis(new CategoryAxis().boundaryGap(false).data(data));
        }
        option.yAxis(new ValueAxis());

        Iterator<Manuscript> iteratorData = list.iterator();
        len = 0;
        switch (type) {
            case "articles":
                while (iteratorData.hasNext()) {
                    Manuscript manuscript = iteratorData.next();
                    data[len] = manuscript.getArticles();
                    len ++;
                }
                option.series(new Line().smooth(false).name("发稿量").stack("总量").symbol(Symbol.droplet).data(data));
                break;
            case "hits":
                while (iteratorData.hasNext()) {
                    Manuscript manuscript = iteratorData.next();
                    data[len] = manuscript.getHits();
                    len ++;
                }
                option.series(new Line().smooth(false).name("点击量").stack("总量").symbol(Symbol.droplet).data(data));
                System.out.println(data);
                break;
            case "comments":
                while (iteratorData.hasNext()) {
                    Manuscript manuscript = iteratorData.next();
                    data[len] = manuscript.getComments();
                    len ++;
                }
                option.series(new Line().smooth(false).name("评论量").stack("总量").symbol(Symbol.droplet).data(data));
                break;
            case "likes":
                while (iteratorData.hasNext()) {
                    Manuscript manuscript = iteratorData.next();
                    data[len] = manuscript.getLikes();
                    len ++;
                }
                option.series(new Line().smooth(false).name("点赞量").stack("总量").symbol(Symbol.droplet).data(data));
        }
        return option;
    }

    @Override
    public Option getLines() {
        Object[] articlesData = new Object[7];
        Object[] hitsData = new Object[7];

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar formerDay = Calendar.getInstance();
        Calendar latterDay = Calendar.getInstance();
        Date date = new Date();
        formerDay.setTime(date);
        latterDay.setTime(date);
        formerDay.set(Calendar.HOUR, -48);
        Date formerDate = formerDay.getTime();
        Date latterDate = latterDay.getTime();
        String former = dateFormat.format(formerDate);
        String latter = dateFormat.format(latterDate);
        Integer articles = dao.count(Article.class, Cnd.where("publish_date", ">", former).and("publish_date", "<", latter).and("del_flag", "!=", 3));
        Integer hits = dao.func(Article.class, "sum", "hits", Cnd.where("publish_date", ">", former).and("publish_date", "<", latter).and("del_flag", "!=", 3));
        articlesData[6] = articles;
        hitsData[6] = hits;
        for (int i = 5; i >= 0; i--) {
            formerDay.set(Calendar.HOUR, -24);
            latterDay.set(Calendar.HOUR, -24);
            formerDate = formerDay.getTime();
            latterDate = latterDay.getTime();
            former = dateFormat.format(formerDate);
            latter = dateFormat.format(latterDate);
            articles = dao.count(Article.class, Cnd.where("publish_date", ">", former).and("publish_date", "<", latter).and("del_flag", "!=", 3));
            hits = dao.func(Article.class, "sum", "hits", Cnd.where("publish_date", ">", former).and("publish_date", "<", latter).and("del_flag", "!=", 3));
            articlesData[i] = articles;
            hitsData[i] = hits;
        }

        //获取日期
        Object[] xDate = new Object[7];
        for (int i = 0; i < 7; i++) {
            formerDay.set(Calendar.HOUR, 24);
            formerDate = formerDay.getTime();
            former = dateFormat.format(formerDate);
            xDate[i] = former;
        }

        Option option = new Option();
        option.tooltip().trigger(Trigger.axis);
        option.toolbox().show(true);
        option.calculable(true);
        option.xAxis(new CategoryAxis().boundaryGap(false).data(xDate));

        option.yAxis(new ValueAxis());
        option.series(new Line().smooth(false).name("发稿量").symbol(Symbol.droplet).data(articlesData),
                      new Line().smooth(false).name("点击量").symbol(Symbol.droplet).data(hitsData));
        return option;
    }

    @Override
    public QueryResult listPage(Integer pageNumber, Integer pageSize, String startTime, String endTime, String type) {
        return articleService.listPage(pageNumber, pageSize, startTime, endTime, type);
    }

    @Override
    public Map<String, Integer> sum() {
        Integer articles = dao.count(Article.class, Cnd.where("del_flag", "!=", 3));
        Integer hits = dao.func(Article.class, "sum", "hits", Cnd.where("del_flag", "!=", 3));
        Integer comments = dao.func(Article.class, "sum", "comments", Cnd.where("del_flag", "!=", 3));
        Map<String, Integer> map = new HashMap<>();
        map.put("articles", articles);
        map.put("hits", hits);
        map.put("comments", comments);
        return map;
    }

    @Override
    public List<Article> hotNews() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar date = Calendar.getInstance();
        date.roll(Calendar.DATE, -7);//日期回滚7天
        String former = df.format(date.getTime());
        Sql sql = Sqls.create("select title, publish_date, hits from cms_article where publish_date >= @former order by hits desc limit 10");
        sql.params().set("former", former);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Article.class));
        dao.execute(sql);
        List<Article> list = sql.getList(Article.class);
        return list;
    }

    @Override
    public Option articleTypes() {
        Sql sql = Sqls.create("select type, count(type) as id from cms_article group by type");
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Article.class));
        dao.execute(sql);
        List<Article> list = sql.getList(Article.class);
        Object[] objects = new Object[list.size()];
        Iterator<Article> iterator = list.iterator();
        int len = 0;
        String type;
        while (iterator.hasNext()) {
            Article article = iterator.next();
            List<Dict> dicts = dictService.query(article.getType(), "sys_code");
            if(null == dicts || dicts.size() < 1){
                continue;
            }
            type = dicts.get(0).getLabel();
            objects[len] = new PieData(type + " ", article.getId());
            len++;
        }
        Option option = new Option();
        option.title().text("稿件类型分布").x(X.center);
        Pie pie = new Pie();
        String[] radius = new String[2];
        radius[0] = "15%";
        radius[1] = "60%";
        pie.roseType(RoseType.area);
        pie.radius(radius);
        pie.data(objects);
        option.series(pie);
        return option;
    }

    public static String stampToDate(Date stamp){
        String date;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        date = simpleDateFormat.format(stamp);
        return date;
    }

    private List<Manuscript> getData(String type, String startTime, String endTime, String size) {
        Sql sql = null;
        List<Manuscript> list = null;
        switch (size) {
            case "day":
                sql = Sqls.create("select $type, create_date from analysis_manuscript where create_date >= @startTime && create_date <= @endTime");
                sql.vars().set("type", type);
                sql.params().set("startTime", startTime);
                sql.params().set("endTime", endTime);
                sql.setCallback(Sqls.callback.entities());
                sql.setEntity(dao.getEntity(Manuscript.class));
                dao.execute(sql);
                list = sql.getList(Manuscript.class);
                return list;
            case "week":
                sql = Sqls.create("select ceil(id/7) as count, sum($type) as $type, create_date from analysis_manuscript where create_date >= @startTime && create_date <= @endTime group by count");
                sql.vars().set("type", type);
                sql.params().set("startTime", startTime);
                sql.params().set("endTime", endTime);
                sql.setCallback(Sqls.callback.entities());
                sql.setEntity(dao.getEntity(Manuscript.class));
                dao.execute(sql);
                list = sql.getList(Manuscript.class);
                return list;
            case "month":
                sql = Sqls.create("select ceil(id/31) as count, sum($type) as $type, create_date from analysis_manuscript where create_date >= @startTime && create_date <= @endTime group by count");
                sql.vars().set("type", type);
                sql.params().set("startTime", startTime);
                sql.params().set("endTime", endTime);
                sql.setCallback(Sqls.callback.entities());
                sql.setEntity(dao.getEntity(Manuscript.class));
                dao.execute(sql);
                list = sql.getList(Manuscript.class);
                return list;
        }
        return null;
    }

    /**
     * 构造一个定时器，定时往Manuscript表里存入数据
     * 每天凌晨00:05查询并插入前一天数据
     */
    @Scheduled(cron = "0 5 0 * * ?")
    @Transactional
    public void insertManuscript() {
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println("当前时间为:" + localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "，文章数据存入了analysis_manuscript表中");

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
        Integer articles = dao.count(Article.class, Cnd.where("publish_date", ">", former).and("publish_date", "<", latter).and("del_flag", "!=", 3));
        Integer hits = dao.func(Article.class, "sum", "hits", Cnd.where("publish_date", ">", former).and("publish_date", "<", latter).and("del_flag", "!=", 3));
        Integer comments = dao.func(Article.class, "sum", "comments", Cnd.where("publish_date", ">", former).and("publish_date", "<", latter).and("del_flag", "!=", 3));
        Integer likes = dao.func(Article.class, "sum", "likes", Cnd.where("publish_date", ">", former).and("publish_date", "<", latter).and("del_flag", "!=", 3));

        Manuscript manuscript = new Manuscript();
        manuscript.setArticles(articles);
        manuscript.setHits(hits);
        manuscript.setComments(comments);
        manuscript.setLikes(likes);
        manuscript.setCreateAt(formerDate);

        dao.insert(manuscript);
    }

}
