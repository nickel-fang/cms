package cn.people.cms.modules.block.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.block.service.IBlockService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Created by lml on 2018/4/12.
 */
@Api(description = "区块管理(block)")
@RestController
@RequestMapping("/api/cms/block")
@Slf4j
public class BlockController {

    @Autowired
    private IBlockService blockService;

    @DeleteMapping(value = "/{id}")
    @RequiresPermissions("cms:block:edit")
    public Result delete(@PathVariable Integer id) {
        if (blockService.delete(id) > 0) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }

    @GetMapping(value = "/list")
    @RequiresPermissions("cms:block:view")
    public Result list(@RequestParam Integer templateId) {
        try {
            return Result.success(blockService.getBlockListByTid(templateId));
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("获取模板区块失败");
        }
    }
}
