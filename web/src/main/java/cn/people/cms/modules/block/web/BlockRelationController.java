package cn.people.cms.modules.block.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.block.model.VO.BlockRelationVO;
import cn.people.cms.modules.block.service.IBlockRelationService;
import cn.people.cms.modules.block.web.vo.OperateVO;
import cn.people.cms.modules.cms.service.IArticleService;
import cn.people.cms.modules.sys.service.ICategoryService;
import cn.people.cms.modules.templates.service.ITemplateService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Created by lml on 2018/4/12.
 */
@Api(description = "区块管理(block)")
@RestController
@RequestMapping("/api/cms/block/relation")
@Slf4j
public class BlockRelationController {

    @Autowired
    private IBlockRelationService blockRelationService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IArticleService articleService;
    @Autowired
    private ITemplateService templateService;


    @PostMapping
    @RequiresPermissions("cms:block:edit")
    public Result relation(@RequestBody BlockRelationVO relationVO) {
        try {
            blockRelationService.saveRelation(relationVO);
            return Result.success();
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("保存失败");
        }
    }

    @PostMapping(value = "/menu")
    @RequiresPermissions("cms:block:edit")
    public Result menuSave(@RequestBody BlockRelationVO relationVO) {
        try {
            if(relationVO.getBlockId() ==null || relationVO.getCategoryId() ==null){
                return Result.error("参数有误，保存失败");
            }
            if(Lang.isEmpty(relationVO.getIds())){
                return Result.success();
            }
            blockRelationService.saveMenu(relationVO);
            return Result.success();
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("保存失败");
        }
    }

    @PostMapping(value = "/input")
    @RequiresPermissions("cms:block:edit")
    public Result inputSave(@RequestBody BlockRelationVO relationVO) {
        try {
            if(relationVO.getBlockId() ==null || relationVO.getCategoryId() ==null){
                return Result.error("参数有误，保存失败");
            }
            if(Lang.isEmpty(relationVO.getInput())){
                return Result.success();
            }
            blockRelationService.saveInput(relationVO);
            return Result.success();
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("保存失败");
        }
    }

    @PostMapping(value = "/article")
    @RequiresPermissions("cms:block:edit")
    public Result articleSave(@RequestBody BlockRelationVO relationVO) {
        try {
            if(relationVO.getBlockId() ==null || relationVO.getCategoryId() ==null){
                return Result.error("参数有误，保存失败");
            }
            if(Lang.isEmpty(relationVO.getItems())){
                return Result.success();
            }
            blockRelationService.saveArticle(relationVO);
            return Result.success();
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("保存失败");
        }
    }

    @PostMapping(value = "/images")
    @RequiresPermissions("cms:block:edit")
    public Result imageSave(@RequestBody BlockRelationVO relationVO) {
        try {
            if(relationVO.getBlockId() ==null || relationVO.getCategoryId() ==null){
                return Result.error("参数有误，保存失败");
            }
            if(Lang.isEmpty(relationVO.getImage())){
                return Result.success();
            }
            blockRelationService.saveImage(relationVO);
            return Result.success();
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("保存失败");
        }
    }

    @GetMapping(value = "/list")
//    @RequiresPermissions("cms:block:view")
    public Result list(@RequestParam Integer blockId, @RequestParam Integer categoryId,
                       @RequestParam Integer pageNumber,@RequestParam Integer pageSize) {
        try {
            QueryResult result = blockRelationService.info(blockId,categoryId,pageNumber,pageSize);
            return Result.success(result);
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("获取数据失败");
        }
    }

    @PostMapping(value = "/del")
    @RequiresPermissions("cms:block:edit")
    public Result delete(@RequestBody OperateVO operateVO) {
        if (blockRelationService.delete(operateVO.getId()) > 0) {
            String str = categoryService.generateMapAndTemplates(operateVO.getTemplateId(),operateVO.getCategoryId());
            if(str ==null){
                return Result.error("数据更新失败");
            }
            return Result.success(str);
        } else {
            return Result.error("删除失败");
        }
    }

    /**
     * 批量切换上下线
     */
    @PostMapping(value = "/batchOnOff")
    @RequiresPermissions("cms:block:edit")
    public Result batchOnOff(@RequestBody OperateVO operateVO) {
        try {
            if(operateVO.getIds() ==null || operateVO.getIds().size()==0 ){
                return Result.success();
            }
            if(operateVO.getTemplateId()==null || operateVO.getCategoryId() == null || operateVO.getDelFlag() == null){
                return Result.error("参数有误，切换失败");
            }
            operateVO.getIds().forEach(id -> blockRelationService.changeStatus(id,operateVO.getDelFlag()));
            String str = categoryService.generateMapAndTemplates(operateVO.getTemplateId(),operateVO.getCategoryId());
            if(str ==null){
                return Result.error("数据更新失败");
            }
            return Result.success(str);
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("批量切换状态失败");
        }
    }

    @PostMapping(value = "/batchDelete")
    @RequiresPermissions("cms:block:edit")
    public Result batchDelete(@RequestBody OperateVO operateVO) {
        try {
            if(operateVO.getIds() ==null || operateVO.getIds().size()==0 ){
                return Result.success();
            }
            if(operateVO.getTemplateId()==null || operateVO.getCategoryId() == null){
                return Result.error("参数有误，删除失败");
            }
            operateVO.getIds().forEach(id -> blockRelationService.delete(id));
            String str = categoryService.generateMapAndTemplates(operateVO.getTemplateId(),operateVO.getCategoryId());
            if(str ==null){
                return Result.error("数据更新失败");
            }
            return Result.success(str);
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("批量删除失败");
        }
    }

    @GetMapping(value = "/status")
    @RequiresPermissions("cms:block:edit")
    public Result status(@RequestParam Integer id,@RequestParam Integer categoryId,@RequestParam Integer templateId) {
        try {
            blockRelationService.changeStatus(id);
            String str = categoryService.generateMapAndTemplates(templateId,categoryId);
            if(str ==null){
                return Result.error("数据更新失败");
            }
            return Result.success(str);
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("更改状态失败");
        }
    }

    @GetMapping(value = "/preview")
    @RequiresPermissions("cms:block:edit")
    public Result preview(@RequestParam Integer categoryId,@RequestParam Integer templateId) {
        try {
            String str = categoryService.generateMapAndTemplates(templateId,categoryId);
            if(str ==null){
                return Result.error("数据更新失败,请检查模板和区块配置");
            }
            return Result.success(templateService.getUrl(str));
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("预览失败");
        }
    }

    @RequestMapping(value = "/batchSort", method = RequestMethod.POST)
    @RequiresPermissions("cms:block:edit")
    public Result batchSort(@RequestBody OperateVO operateVO) {
        if(operateVO.getTemplateId()== null || operateVO.getCategoryId() == null){
            return Result.error("参数有误");
        }
        if(Lang.isEmpty(operateVO.getList())){
            return Result.success();
        }
        blockRelationService.batchUpdate(operateVO.getList());
        categoryService.updateCategoryTemplateAsync(categoryService.getSimpleCategory(operateVO.getCategoryId()));
        return Result.success();
    }

    @RequestMapping(value = "/details", method = RequestMethod.GET)
    @RequiresPermissions("cms:block:edit")
    public Result updateDetails(@RequestParam Integer categoryId) {
        if(categoryId == null){
            return Result.error("参数有误");
        }
        try {
            categoryService.manuallyUpdate(categoryId,false,null);
            return Result.success();
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            return Result.error("数据更新失败");
        }
    }


}
