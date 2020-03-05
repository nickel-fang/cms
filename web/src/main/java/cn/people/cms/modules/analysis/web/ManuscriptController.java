package cn.people.cms.modules.analysis.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.analysis.service.IManuscriptService;
import com.github.abel533.echarts.Option;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cuiyukun on 2017/6/30.
 */
@Api(description = "稿件统计(analysis模块)")
@RestController
@RequestMapping("/api/analysis/script/")
@Slf4j
public class ManuscriptController {

    @Autowired
    private IManuscriptService manuscriptService;

    @RequestMapping(value = "/line", method = RequestMethod.GET)
    @RequiresPermissions("analysis:script:view")
    public Result script(@RequestParam String type, @RequestParam String startTime, @RequestParam String endTime, @RequestParam String size) {
        if (Lang.isEmpty(type)) {
            return Result.error("展示类型为空！");
        }
        Option option = manuscriptService.getScriptLine(type, startTime, endTime, size);
        return Result.success(option);
    }

    @RequestMapping(value = "/lines", method = RequestMethod.GET)
    @RequiresPermissions("analysis:script:view")
    public Result lines() {
        Option option = manuscriptService.getLines();
        return Result.success(option);
    }

    @RequestMapping(value = "/chart", method = RequestMethod.GET)
    @RequiresPermissions("analysis:script:view")
    public Result quantityChart(@RequestParam String type, @RequestParam Integer pageNumber, @RequestParam Integer pageSize, @RequestParam String startTime, @RequestParam String endTime) {
        if (Lang.isEmpty(type)) {
            return Result.error("类型为空！");
        }
        QueryResult stat = manuscriptService.listPage(pageNumber, pageSize, startTime, endTime, type);
        return Result.success(stat);
    }

    /**
     * 仪表盘数据总量
     * @return
     */
    @RequestMapping(value = "/sum", method = RequestMethod.GET)
    @RequiresPermissions("analysis:script:view")
    public Result sum() {
        Map<String, Integer> map = manuscriptService.sum();
        Map<Integer,Map> result = new HashMap<>();
        result.put(0, map);
        return Result.success(result);
    }

    /**
     * 今日热点新闻
     */
    @RequestMapping(value = "/news", method = RequestMethod.GET)
    @RequiresPermissions("analysis:script:view")
    public Result hotNews() {
        List list = manuscriptService.hotNews();
        return Result.success(list);
    }

    /**
     * 稿件类型饼状图
     */
    @RequestMapping(value = "/types", method = RequestMethod.GET)
    @RequiresPermissions("analysis:script:view")
    public Result articleTypes() {
        Option option = manuscriptService.articleTypes();
        return Result.success(option);
    }
}
