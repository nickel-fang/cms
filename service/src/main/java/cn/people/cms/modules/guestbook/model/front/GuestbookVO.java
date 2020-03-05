package cn.people.cms.modules.guestbook.model.front;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by sunday on 2017/4/11.
 */
@Data
public class GuestbookVO {
    private Integer type;//分类
    private String content;//内容
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date beginTime;//查询起始时间
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endTime;//查询截止时间
}
