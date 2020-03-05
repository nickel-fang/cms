package cn.people.cms.modules.sys.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.sys.model.CategoryModel;
import cn.people.cms.modules.sys.service.ICategoryModelService;
import io.swagger.annotations.Api;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by lml on 2017/5/3.
 */
@Api(description = "栏目模型管理(sys模块)")
@RestController
@RequestMapping("/api/sys/category/model")
public class CategoryModelController {

    @Autowired
    private ICategoryModelService categoryModelService;

    @RequestMapping(method = RequestMethod.GET)
    @RequiresPermissions("sys:catgModel:view")
    public Result list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return Result.success(categoryModelService.listPage(pageNumber, pageSize));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("sys:catgModel:view")
    public Result view(@PathVariable Integer id) {
        CategoryModel categoryModel = categoryModelService.fetch(id);
        return Result.success(categoryModel);
    }

    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("sys:catgModel:edit")
    public Result save(@RequestBody CategoryModel categoryModel) {
        if (!Lang.isEmpty(categoryModelService.save(categoryModel))) {
            return Result.success();
        }
        return Result.error("保存失败");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("sys:catgModel:edit")
    public Result delete(@PathVariable Integer id) {
        if (categoryModelService.vDelete(id) > 0) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }

    @RequestMapping(value = "/batchDelete",method = RequestMethod.POST)
    @RequiresPermissions("sys:catgModel:edit")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        if(!Lang.isEmpty(ids)){
            ids.forEach(id->categoryModelService.vDelete(id));
        }
        return Result.success();
    }


    @RequestMapping(method = RequestMethod.PATCH)
    @RequiresPermissions("sys:catgModel:edit")
    public Result update(@RequestBody CategoryModel categoryModel) {
        if (categoryModelService.updateIgnoreNull(categoryModel)>0) {
            return Result.success();
        }
        return Result.error("更新失败");
    }

    @RequestMapping(value = "/all",method = RequestMethod.GET)
    @RequiresPermissions("sys:catgModel:edit")
    public Result getAll(){
        return Result.success(categoryModelService.getAll());
    }

}
