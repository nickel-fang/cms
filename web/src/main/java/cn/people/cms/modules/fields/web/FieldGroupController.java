package cn.people.cms.modules.fields.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.fields.IFieldGroupService;
import cn.people.cms.modules.fields.model.FieldGroup;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字段组Controller
 *
 * @author lml
 */
@Api(description = "字段组管理(fields模块)")
@RestController
@RequestMapping("/api/fields/field/group/")
@Slf4j
public class FieldGroupController {

    @Autowired
    private IFieldGroupService fieldGroupService;

    @RequestMapping(method = RequestMethod.GET)
    @RequiresPermissions("fields:fieldGroup:view")
    public Result list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return Result.success(fieldGroupService.listPage(pageNumber, pageSize));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("fields:fieldGroup:view")
    public Result view(@PathVariable Integer id) {
        FieldGroup fieldGroup = fieldGroupService.fetch(id);
        return Result.success(fieldGroup);
    }

    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("fields:fieldGroup:edit")
    public Result save(@RequestBody FieldGroup fieldGroup) {
        fieldGroupService.save(fieldGroup);
        return Result.success(fieldGroup);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("fields:fieldGroup:edit")
    public Result delete(@PathVariable Integer id) {
        return Result.success(fieldGroupService.delete(id));
    }

    @RequestMapping(value = "/batchDelete",method = RequestMethod.POST)
    @RequiresPermissions("fields:fieldGroup:edit")
    public Result batchDelete(@RequestBody List<String> ids) {
        ids.forEach(id->fieldGroupService.delete(Integer.parseInt(id)));
        return Result.success();
    }

}
