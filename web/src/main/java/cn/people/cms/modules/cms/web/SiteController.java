package cn.people.cms.modules.cms.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.cms.model.Site;
import cn.people.cms.modules.cms.model.front.SiteVO;
import cn.people.cms.modules.cms.service.ISiteService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Created by lml on 2018/3/27.
 */
@Api(description = "站点管理(cms模块)")
@RestController
@RequestMapping("/api/cms/site")
@Slf4j
public class SiteController {

    @Autowired
    private ISiteService siteService;

    @GetMapping
    @RequiresPermissions("cms:site:view")
    public Result list(SiteVO siteVO) {
        return Result.success(siteService.findByVO(siteVO));
    }

    @GetMapping("/all")
//    @RequiresPermissions("cms:site:view")  //由于三员特殊性，这里需要放开
    public Result list(@RequestParam(required = false) Boolean role) {
        List list = siteService.all(role);
        return Result.success(list);
    }

    @GetMapping(value = "/{id}")
    @RequiresPermissions("cms:site:view")
    public Result view(@PathVariable Integer id) {
        return Result.success(siteService.fetch(id));
    }

    @PostMapping
    @RequiresPermissions("cms:site:edit")
    public Result save(@RequestBody Site site) {
        if(!StringUtils.isBlank(site.getSlug())){
            Site ori = siteService.fetch(site.getSlug());
            if (site.getId() != null) {
                if(!ori.getId().equals(site.getId())){
                    return Result.error("别名已经使用");
                }
            } else {
                if(ori !=null){
                    return Result.error("别名已经使用");
                }
            }
        }
        return Result.success(siteService.save(site));
    }

    @DeleteMapping(value = "/{id}")
    @RequiresPermissions("cms:site:edit")
    public Result delete(@PathVariable Integer id) {
        return Result.success(siteService.delete(id));
    }
}
