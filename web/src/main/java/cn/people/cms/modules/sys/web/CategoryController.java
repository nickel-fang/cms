package cn.people.cms.modules.sys.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.sys.model.Category;
import cn.people.cms.modules.sys.service.ICategoryService;
import cn.people.cms.modules.templates.service.ITemplateService;
import cn.people.cms.modules.util.HttpUtils;
import cn.people.cms.util.base.ShiroUtils;
import cn.people.domain.IUser;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Created by lml on 2016/12/26.
 */
@Api(description = "类目管理(sys模块)")
@RestController
@Slf4j
@RequestMapping("/api/sys/category")
public class CategoryController {

    @Value("${theone.project.rootId}")
    private Integer rootId;

    @Value("${theone.forums.url}")
    private String forumsUrl ;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private ITemplateService templateService;

    @Value("${theone.phpPerformance.url}")
    private String phpPerformanceUrl;


    @RequestMapping(method = RequestMethod.GET)
    @RequiresPermissions("sys:category:view")
    public Result list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return Result.success(categoryService.listPage(pageNumber, pageSize));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("sys:category:view")
    public Result view(@PathVariable Integer id) {
        return Result.success(categoryService.fetch(id));
    }

    @GetMapping("/getBBSInfo")
    @RequiresPermissions("sys:category:view")
    public Result getBBSInfo(){

        String forumsData = HttpUtils.doGet(forumsUrl+"/api.php?mod=forums"); //获取论坛板块信息
        log.info("forumsData:{}",forumsData);
        if(StringUtils.isNotBlank(forumsData)){
            return Result.success(forumsData);
        }
        return Result.error("获取论坛(bbs)板块信息失败！");
    }


    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("sys:category:edit")
    public Result save(@RequestBody Category category) {
        if (!Lang.isEmpty(category)) {
            boolean hasId=false;
            if (category.getId()!=null){
                hasId=true;
            }
            Category ca = (Category)categoryService.save(category);
            if(Category.STATUS_ONLINE  == category.getDelFlag()){
                categoryService.updateCategoryTemplateAsync(category);
            }
            try {
                boolean finalHasId = hasId;
                Runnable task=() ->{
                    Map<String,Object> map=new HashMap<>();
                    map.put("name",ca.getName());
                    if (!finalHasId){
                        map.put("id",ca.getId());
                        map.put("siteId",ca.getSiteId());
                        map.put("parentId",ca.getParentId());
                        String jsonString = JSON.toJSONString(map);
                        HttpUtils.doPost(phpPerformanceUrl+"/channel",jsonString);
                    }else {
                        String jsonString = JSON.toJSONString(map);
                        HttpUtils.doPut(phpPerformanceUrl+"/channel/"+category.getId(),jsonString);
                    }
                };
                new Thread(task).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Result.success(category);
        }
        return Result.error("保存失败");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("sys:category:edit")
    public Result delete(@PathVariable Integer id) {
        if(id == 1){
            return Result.error("顶级菜单不允许删除");
        }
        if (categoryService.delete(id) > 0) {
            try {
                Runnable task= () ->{
                    HttpUtils.doDelete(phpPerformanceUrl+"/channel/"+id);
                };
                new Thread(task).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 部分更新
     *
     * @param category
     * @return
     */
    @RequestMapping(method = RequestMethod.PATCH)
    @RequiresPermissions("sys:category:edit")
    public Result update(@RequestBody Category category) {
        if (categoryService.save(category)!=null) {
            if(Category.STATUS_ONLINE  == category.getDelFlag()){
                categoryService.updateCategoryTemplateAsync(category);
            }
            return Result.success();
        }
        return Result.error("更新失败");
    }

    /**
     * 栏目列表
     *
     * @return
     */
    @RequestMapping(value = "/tree", method = RequestMethod.GET)
//    @RequiresPermissions("sys:category:view")  //由于三员特殊性，这里需要放开
    public Result getTree(@RequestParam(required = false) Integer siteId, @RequestParam(required = false) Boolean role) {
        if(siteId == null){
            return Result.error("站点不能为空");
        }
        IUser user = ShiroUtils.getUser();
        if(user == null || user.getId()==null){
            return Result.error(Result.NOT_LOGGED_IN_CODE_ERROR,"非登录用户");
        }
        return Result.success(categoryService.getTree(rootId,siteId, role));
    }

    @RequestMapping(value = "/batchSort", method = RequestMethod.POST)
    @RequiresPermissions("sys:category:edit")
    public Result batchSort(@RequestBody List<Category> list) {
        categoryService.batchSort(list);
        return Result.success();
    }

    @GetMapping(value = "/manually/list")
    public Result manuallyUpdateList(@RequestParam Integer cid,@RequestParam(required = false,defaultValue = "false") Boolean isOwn) {
        try {
            if(cid == null){
                return Result.error("参数有误");
            }
            categoryService.manuallyUpdate(cid,isOwn,null);
            return Result.success();
        }catch (Exception ex){
            log.error(ex.getMessage());
            return Result.error("更新列表失败");
        }
    }

    @GetMapping(value = "/manually/flush")
    public Result manuallyUpdateList(@RequestParam Integer siteId,@RequestParam Integer newPid) {
        try {
            categoryService.flushCategories(siteId,newPid);
            return Result.success();
        }catch (Exception ex){
            log.error(ex.getMessage());
            return Result.error("更新列表失败");
        }
    }

    @GetMapping(value = "/preview/list")
    @RequiresPermissions("sys:category:edit")
    public Result preview(@RequestParam Integer id) {
        try {
            if(id == null){
                return Result.error("参数有误");
            }
            String url = categoryService.dynamicUpdateById(categoryService.getSimpleCategory(id),"preview");
            return Result.success(templateService.getUrl(url));
        }catch (Exception ex){
            log.error(ex.getMessage());
            return Result.error("预览失败");
        }
    }
}
