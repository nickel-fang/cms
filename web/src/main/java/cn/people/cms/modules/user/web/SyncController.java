package cn.people.cms.modules.user.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.user.model.User;
import cn.people.cms.modules.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户同步controller
 * Created by cuiyukun on 2018/3/19.
 */
@Slf4j
@RestController
@RequestMapping("/api/users/sync")
public class SyncController {

    @Autowired
    private IUserService userService;

    @PostMapping("/add")
    public Result add(@RequestBody User user) {
        User oriUser = userService.fetch(user.getUsername());
        if (null != oriUser) {
            return Result.error("用户已存在！");
        }
        user.setUserId(user.getId());
        user.setId(null);
        User newUser = (User)userService.save(user);
        if (null != user) {
            return Result.success();
        }
        return Result.error("同步失败！");
    }

    @PostMapping("/edit")
    public Result edit(@RequestBody User user) {
        User oriUser = userService.fetch(user.getUsername());
        if (null == oriUser) {
            return Result.error("用户不存在！");
        }
        user.setUserId(user.getId());
        user.setId(null);
        Integer count = userService.update(user, null);
        if (1 == count) {
            return Result.success();
        }
        return Result.error("更新失败！");
    }

}
