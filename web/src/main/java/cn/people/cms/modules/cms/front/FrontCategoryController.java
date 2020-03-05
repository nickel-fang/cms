package cn.people.cms.modules.cms.front;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.sys.model.Category;
import cn.people.cms.modules.sys.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/front/category")
public class FrontCategoryController {

    @Autowired
    private ICategoryService categoryService ;

    /**
     * 获取所有频道信息
     * @param
     * @return
     */
    @GetMapping("/getCategoryList")
    public Result getList(){

        log.info("into method getList:{}");

        List<Category> categoryList = categoryService.getTree(0);

        log.info("categoryList:{}",categoryList.size());

        return Result.success(categoryList);
    }


}
