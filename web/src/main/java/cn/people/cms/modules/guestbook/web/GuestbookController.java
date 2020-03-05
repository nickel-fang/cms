package cn.people.cms.modules.guestbook.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.guestbook.model.Guestbook;
import cn.people.cms.modules.guestbook.model.front.GuestbookVO;
import cn.people.cms.modules.guestbook.service.IGuestbookService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 反馈管理
 * Created by sunday on 2017/4/11.
 */
@Api(description = "反馈管理")
@RestController
@RequestMapping("/api/guestbook")
@Slf4j
public class GuestbookController {

    @Autowired
    private IGuestbookService guestbookService;

    @RequestMapping(method = RequestMethod.GET)
    @RequiresPermissions("client:guestbook:view")
    public Result list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize, GuestbookVO guestbookVO) {
        return Result.success(guestbookService.findSearchPage(pageNumber, pageSize, guestbookVO));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("client:guestbook:view")
    public Result view(@PathVariable Integer id) {
        return Result.success(guestbookService.fetch(id));
    }

    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("client:guestbook:edit")
    public Result save(@RequestBody Guestbook guestbook) {
        guestbookService.save(guestbook);
        return Result.success(guestbook);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    @RequiresPermissions("client:guestbook:edit")
    public Result updateIgnoreNull(@RequestBody Guestbook guestbook) {
        guestbookService.updateIgnoreNull(guestbook);
        return Result.success(guestbook);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("client:guestbook:edit")
    public Result delete(@PathVariable Integer id) {
        return Result.success(guestbookService.delete(id));
    }

}
