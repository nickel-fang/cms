package cn.people.cms.modules.user.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.user.model.Role;
import cn.people.cms.modules.user.service.IRoleService;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//import org.springframework.security.access.prepost.PreAuthorize;

/**
 * 用户管理下角色管理Controller
 */
@RestController
@RequestMapping("/api/users/role")
@Slf4j
public class RoleController {

	@Autowired
	private IRoleService roleService;


    /**
     * 获取全部角色
     */
	@GetMapping("listAll")
//    @RequiresPermissions("users:role:view")
    public Result listAll(){
        return Result.success(roleService.listAll());
    }

	/**
	 * 分页获取角色列表
	 */
	@GetMapping("list")
//    @RequiresPermissions("users:role:view")
	public Result list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
		return Result.success(roleService.listPage(pageNumber, pageSize));
	}

	/**
	 * 根据id获取角色
	 */
	@GetMapping("{id}")
//    @RequiresPermissions("users:role:view")
	public Result getRoleById(@PathVariable Integer id) {
		if (Lang.isEmpty(roleService.fetch(id))) {
			return Result.error(-1,"该角色不存在！");
		}
		return Result.success(roleService.fetch(id));
	}

	/**
	 * 添加一条角色
	 */
	@PostMapping("add")
//    @RequiresPermissions("users:role:edit")
	public Result roleAdd(@RequestBody Role role) {
		String name = role.getName();
		if (!Lang.isEmpty(roleService.fetch(name))) {
			return Result.error(-1,"该角色已存在！");
		}
		roleService.save(role);
		return Result.success();
	}

	/**
	 * 修改一条角色信息
	 */
	@PatchMapping("update")
//    @RequiresPermissions("users:role:edit")
	public Result roleUpdate(@RequestBody Role role) {
		Integer id = role.getId();
		if (Lang.isEmpty(roleService.fetch(id))) {
			return Result.error(-1,"该角色不存在！");
		}
		if (null != roleService.update(role)) {
			return Result.success();
		}
		return Result.error(-2,"更新失败！");
	}

	/**
	 * 伪删除角色
	 * 不删除记录，只修改del_flag字段值
	 */
	@DeleteMapping("{id}")
//    @RequiresPermissions("users:role:edit")
	public Result roleVDel(@PathVariable Integer id) {
		if (Lang.isEmpty(roleService.fetch(id))) {
			return Result.error(-1,"该角色不存在！");
		}
		roleService.vDelete(id);
		return Result.success();
	}
}
