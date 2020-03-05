package cn.people.cms.modules.file.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.file.model.MediaInfo;
import cn.people.cms.modules.file.service.IMediaInfoService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.sql.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;

/**
 * 媒体信息Controller
 *
 * @author zxz
 */
@Api(description = "媒体资源管理")
@RestController
@RequestMapping("/api/file/media/info/")
@Slf4j
public class MediaInfoController {

	@Autowired
	private IMediaInfoService mediaInfoService;

	@RequestMapping(method = RequestMethod.GET)
	public Result list(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String startTime,
					   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String endTime,
					   @RequestParam(required = false) String keyword,
					   @RequestParam String type,
					   @RequestParam Integer pageNumber,
					   @RequestParam Integer pageSize) {
		Criteria criteria = Cnd.cri();
		criteria.where().and("status", "=", 1).and("del_flag", "=", 0).and("type", "=", type);
		if (StringUtils.isNotBlank(keyword)) {
			criteria.where().and("name", "like", "%" + keyword + "%").or("keyword", "like", "%" + keyword + "%");
		}
		if (null != startTime && null == endTime) {
			criteria.where().andBetween("trans_time", startTime, new Date());
		}
		if (null != startTime && null != endTime) {
			criteria.where().andBetween("trans_time", startTime, endTime);
		}
		if (null == startTime && null != endTime) {
			Calendar calendar = Calendar.getInstance();
			calendar.set(0, 0, 0);
			criteria.where().andBetween("trans_time", calendar.getTime(), endTime);
		}
		criteria.getOrderBy().desc("create_at");
		return Result.success(mediaInfoService.listPage(pageNumber, pageSize, criteria));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Result view(@PathVariable Integer id) {
		return Result.success(mediaInfoService.fetch(id));
	}

	@RequestMapping(method = RequestMethod.POST)
	public Result save(@RequestBody MediaInfo mediaInfo) {
		mediaInfoService.save(mediaInfo);
		return Result.success(mediaInfo);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Result delete(@PathVariable Integer id) {
		return Result.success(mediaInfoService.delete(id));
	}

}
