package cn.people.cms.modules.sys.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.sys.model.front.LogVO;
import cn.people.cms.modules.sys.service.ILogService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志Controller
 *
 * @author cuiyukun
 */
@Api(description = "日志管理(sys模块)")
@RestController
@RequestMapping("/api/sys/log")
@Slf4j
public class LogController {

	@Autowired
	private ILogService logService;

	@RequestMapping(method = RequestMethod.GET)
    @RequiresPermissions("analysis:log:view")
    public Result list(LogVO logVO){
	    return Result.success(logService.page(logVO));
    }
}
