package cn.people.cms.modules.cms.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.cms.service.impl.VoteOptionService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lml on 2017/3/21.
 */
@Api(description = "调查选项管理(cms模块)")
@RestController
@RequestMapping("/api/cms/voteOption")
@Slf4j
public class VoteOptionController {

	@Autowired
	private VoteOptionService voteOptionService;

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("cms:vote:edit")
	public Result delete(@PathVariable Integer id) {
		if (voteOptionService.delete(id) > 0) {
			return Result.success();
		} else {
			return Result.error("删除选项错误");
		}
	}
}
