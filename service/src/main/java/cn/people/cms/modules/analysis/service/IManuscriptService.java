package cn.people.cms.modules.analysis.service;

import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.analysis.model.Manuscript;
import cn.people.cms.modules.cms.model.Article;
import com.github.abel533.echarts.Option;
import org.nutz.dao.QueryResult;

import java.util.List;
import java.util.Map;

/**
 * 稿件统计service
 *
 * Created by cuiyukun on 2017/6/30.
 */
public interface IManuscriptService extends IBaseService<Manuscript> {

    Option getScriptLine(String type, String startTime, String endTime, String size);

    Option getLines();

    QueryResult listPage(Integer pageNumber, Integer pageSize, String startTime, String endTime, String type);

    List<Article> hotNews();

    Option articleTypes();
    Map<String, Integer> sum();
}
