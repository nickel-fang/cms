package cn.people.cms.modules.user.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.user.model.User;
import cn.people.cms.modules.user.service.IUserService;
import cn.people.cms.util.base.UserUtil;
import cn.people.domain.IUser;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理controller
 * Created by cuiyukun on 2018/6/10.
 */
@RestController
@RequestMapping("/api/users/permission")
@Slf4j
public class PermissionController {

    @Autowired
    private IUserService userService;

    /**
     * 用户列表获取
     */
    @GetMapping("list")
//    @PreAuthorize("hasAuthority('users:user:view')")
    public Result list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize, @RequestParam String username,
                       @RequestParam String name) {
        QueryResult users = userService.listPage(pageNumber, pageSize, username, name);
        List list = users.getList();
        IUser currentUser = UserUtil.getUser();
        if (!Lang.isEmpty(currentUser)) {
            if (!currentUser.isAdmin()) {
                list.remove(0);
                users.setList(list);
            }
        }
        return Result.success(users);
    }

    /**
     * 根据id获取用户角色权限
     */
    @GetMapping("{id}")
//    @PreAuthorize("hasAuthority('upms:user')")
    public Result getUserById(@PathVariable Integer id) {
        if (Lang.isEmpty(userService.fetch(id))) {
            return Result.error(-1,"该账号不存在！");
        }
        User user = userService.fetch(id);
        return Result.success(user);
    }

    /**
     * 管理员修改用户信息
     */
    @PatchMapping("update")
//    @PreAuthorize("hasAuthority('users:user:edit')")
    public Result userUpdate(@RequestBody User user) {
        if(null == user.getId()){
            return Result.error(-1,"用户id不能为空！");
        }
        if (Lang.isEmpty(userService.fetch(user.getId()))) {
            return Result.error(-2,"该账号不存在！");
        }
        try {
            userService.saveRoleRelation(user);
        } catch (Exception e) {
            return Result.error(-3,"更新失败！");
        }
        return Result.success();
    }

}
