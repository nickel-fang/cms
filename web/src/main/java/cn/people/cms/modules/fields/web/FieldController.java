package cn.people.cms.modules.fields.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.fields.impl.FieldService;
import cn.people.cms.modules.fields.model.Field;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字段描述Controller
 *
 * @author lml
 */
@Api(description = "字段管理(fields模块)")
@RestController
@RequestMapping("/api/fields/field")
@Slf4j
public class FieldController {

    @Autowired
    private FieldService fieldService;

    @RequestMapping(method = RequestMethod.GET)
    @RequiresPermissions("fields:field:view")
    public Result list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return Result.success(fieldService.listPage(pageNumber, pageSize));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("fields:field:view")
    public Result view(@PathVariable Integer id) {
        return Result.success(fieldService.fetch(id));
    }

    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("fields:field:edit")
    public Result save(@RequestBody Field field) {
        return Result.success(fieldService.save(field));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("fields:field:edit")
    public Result delete(@PathVariable Integer id) {
        return Result.success(fieldService.delete(id));
    }

    @RequestMapping(value = "/batchDelete",method = RequestMethod.POST)
    @RequiresPermissions("fields:field:edit")
    public Result batchDelete(@RequestBody List<Integer> ids) {
        if(!Lang.isEmpty(ids)){
            ids.forEach(id->fieldService.delete(id));
        }
        return Result.success();
    }

    /**
     * 部分更新
     *
     * @param field
     * @return
     */
    @RequestMapping(method = RequestMethod.PATCH)
    @RequiresPermissions("fields:field:edit")
    public Result update(@RequestBody Field field) {
        fieldService.updateIgnoreNull(field);
        return Result.success();
    }

    @RequestMapping(value = "/exist", method = RequestMethod.GET)
    @RequiresPermissions("fields:field:view")
    public Result slugExist(String slug) {
        if(!StringUtils.isNotBlank(slug)){
            return Result.error(-1,"参数为空");
        }
        return Result.success(fieldService.slugExist(slug));
    }

    @RequestMapping(value = "/batchSort", method = RequestMethod.POST)
    @RequiresPermissions("fields:field:edit")
    public Result batchSort(@RequestBody List<Field> list) {
        fieldService.batchSort(list);
        return Result.success();
    }
}
