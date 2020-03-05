package cn.people.cms.modules.test;

import cn.people.cms.modules.sys.service.ICategoryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lml on 2018/3/22.
 */
@Api(description = "测试模块")
@RestController
@RequestMapping("/auth/test")
@Slf4j
public class TestController {
    @Autowired
    private ICategoryService categoryService;
}
