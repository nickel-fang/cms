package cn.people.cms.modules.analysis.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.analysis.service.IEditorService;
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

/**
 * Created by cuiyukun on 2017/6/30.
 */
@Api(description = "人员工作量统计(analysis模块)")
@RestController
@RequestMapping("/api/analysis/editor/")
@Slf4j
public class EditorController {

    @Autowired
    private IEditorService editorService;

    @RequestMapping(value = "/chart", method = RequestMethod.GET)
    @RequiresPermissions("analysis:editor:view")
    public Result quantityChart(@RequestParam String type, @RequestParam Integer pageNumber, @RequestParam String startTime, @RequestParam String endTime) {
        if (Lang.isEmpty(type)) {
            return Result.error("类型为空！");
        }
        QueryResult stat = editorService.listPage(pageNumber, 10, startTime, endTime, type);
        return Result.success(stat);
    }

}
