package cn.people.cms.modules.user.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.user.model.Menu;
import cn.people.cms.modules.user.service.IMenuService;
import cn.people.cms.util.base.ShiroUtils;
import cn.people.cms.util.base.UserUtil;
import cn.people.domain.IUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

//import org.springframework.security.access.prepost.PreAuthorize;

/**
 * 菜单管理Controller
 */
@RestController
@RequestMapping("/api/sys/menu")
@Slf4j
public class MenuController {

	@Autowired
	private IMenuService menuService;

	/**
	 * 获取菜单列表
	 */
	@GetMapping("tree")
	public Result getTree(@RequestParam(required = false) Boolean isAll) {
        IUser user = UserUtil.getUser();
        if (Lang.isEmpty(user)){
            return Result.error(Result.NOT_LOGGED_IN_CODE_ERROR, "用户未登陆");
        }
        List<Menu> menus = menuService.getMenuTree(user.getId(), isAll);
	    return Result.success(menuService.getMenuTree(user.getId(), isAll));
	}

	/**
	 * 获取菜单树
	 */
	@GetMapping("treeView")
	public Result getTreeView(@RequestParam(required = false) Boolean role) {
        IUser user = ShiroUtils.getUser();
        if (Lang.isEmpty(user)){
            return Result.error(Result.NOT_LOGGED_IN_CODE_ERROR, "用户未登陆");
        }
        if (null != role) {
            if (user.getId() == 3 && role) {
                return Result.success(menuService.getMenuTree(true,user.getId(), true)); //安全保密管理员在角色管理中可以看到全部菜单
            }
        }
        return Result.success(menuService.getMenuTree(true,user.getId(), null));
	}

	/**
	 * 根据id获取菜单
	 */
	@GetMapping("{id}")
//    @RequiresPermissions("sys:menu:view")
	public Result getMenuById(@PathVariable Integer id) {
		if (Lang.isEmpty(menuService.fetch(id))) {
			return Result.error("菜单不存在");
		}
		return Result.success(menuService.fetch(id));
	}

	/**
	 * 添加一条菜单
	 */
	@PostMapping
//    @RequiresPermissions("sys:menu:edit")
	public Result menuAdd(@RequestBody Menu menu) {
		menuService.save(menu);
		return Result.success();
	}

	/**
	 * 伪删除菜单
	 * 不删除记录，只修改del_flag字段值
	 */
	@DeleteMapping("{id}")
//    @RequiresPermissions("sys:menu:edit")
	public Result menuVDel(@PathVariable Integer id) {
        if(id == 1){
            return Result.error("顶级菜单不允许删除");
        }
		if (Lang.isEmpty(menuService.fetch(id))) {
			return Result.error("菜单不存在！");
		}
		if (menuService.vDelete(id) > 0) {
			return Result.success();
		}
        return Result.error("删除失败");
	}


	/**
	 * 部分更新
	 */
	@PatchMapping
//    @RequiresPermissions("sys:menu:edit")
	public Result update(@RequestBody Menu menu) {
		menuService.save(menu);
		return Result.success("success");
	}

	/**
	 * 上下线
	 */
	@PatchMapping("/onOff/{id}")
//    @RequiresPermissions("sys:menu:edit")
	public Result onOff(@PathVariable Integer id) {
		menuService.updateIgnoreNull(menuService.changeOnlineStatus(id));
		return Result.success();
	}

	/**
	 * 批量切换上下线
	 */
	@PatchMapping("/batchOnOff")
//    @RequiresPermissions("sys:menu:edit")
	@ResponseBody
	public Result batchOnOff(@RequestParam String menuIds) {
		List<String> ids = null;
		if (StringUtils.isNotEmpty(menuIds)) {
			ids = Arrays.asList(menuIds.split(","));
		}
		if (null != ids && ids.size() > 0) {
			ids.forEach(id -> {
				menuService.updateIgnoreNull(menuService.changeOnlineStatus(Integer.parseInt(id)));
			});
			return Result.success();
		}
		return null;
	}

	@PatchMapping("/batchUpdate")
//    @RequiresPermissions("sys:menu:edit")
	public Result batchUpdate(@RequestBody List<Menu> list) {
		menuService.batchUpdate(list);
		return Result.success();
	}

	@PatchMapping("batchSort")
//    @RequiresPermissions("sys:menu:edit")
	public Result batchSort(@RequestBody List<Menu> list){
		menuService.batchSort(list);
		return Result.success();
	}
}
