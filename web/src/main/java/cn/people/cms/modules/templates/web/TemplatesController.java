package cn.people.cms.modules.templates.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.templates.VO.TemplateVO;
import cn.people.cms.modules.templates.model.Template;
import cn.people.cms.modules.templates.service.ITemplateService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * Created by lml on 2018/1/22.
 */
@Api(description = "模板模块")
@RestController
@RequestMapping("/api/templates")
@Slf4j
public class TemplatesController {

    @Autowired
    private ITemplateService templateService;

    @PostMapping(value = "/files")
    @RequiresPermissions("cms:templates:edit")
    public Result uploads(@RequestParam("file") MultipartFile[] files,@RequestParam Integer siteId) {
        try {
            Map data = templateService.uploadFiles(files,siteId);
            if(data == null || data.size() == 0){
                return Result.error("文件上传失败");
            }
            return Result.success("文件上传成功",data);
        }catch (Exception ex){
            log.error(ex.getMessage());
            return Result.error("文件上传失败");
        }
    }

    @PostMapping
    @RequiresPermissions("cms:templates:edit")
    public Result save(@RequestBody Template template){
        try {
            if(null == template.getSiteId()){
                return Result.error("站点编码不能为空");
            }
            if(null !=templateService.save(template)){
                return Result.success("保存成功");
            }else {
                return Result.error("配置有误，保存失败");
            }
        }catch (Exception ex){
            log.error(ex.getMessage());
            return Result.error("保存失败");
        }
    }

    @DeleteMapping(value = "/{id}")
    @RequiresPermissions("cms:templates:edit")
    public Result delete(@PathVariable Integer id) {
        if (templateService.vDelete(id) > 0) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }

    @GetMapping(value = "/{id}")
    @RequiresPermissions("cms:templates:view")
    public Result view(@PathVariable Integer id) {
        if (id == null) {
            return Result.error("传入参数异常");
        }
        return Result.success(templateService.fetch(id));
    }

    @GetMapping(value = "/list")
    @RequiresPermissions("cms:templates:view")
    public Result list(TemplateVO templateVO) {
        return Result.success(templateService.findByVO(templateVO));
    }


    @GetMapping(value = "/findTempByName")
    @RequiresPermissions("cms:templates:view")
    public Result findTempByName(TemplateVO templateVO){
        return Result.success(templateService.findByVO(templateVO));
    }

    @GetMapping(value = "/findTempDetailByName")
    @RequiresPermissions("cms:templates:view")
    public Result findTempDetailByName(TemplateVO templateVO){
        return Result.success(templateService.findByVO(templateVO));
    }


}
