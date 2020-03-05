package cn.people.cms.modules.sys.model;

import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.Date;

/**
 * User: 张新征
 * Date: 2017/4/11 13:15
 * Description:
 */
@Table("sys_log_${cid}")
@Data
public class Log{
    @Id
    @Comment("主键")
    private Integer id;

    @Column
    @ColDefine(width = 1)
    @Comment("日志类型（1：接入日志；2：错误日志）")
    private String type;

    @Column(hump = true)
    @ColDefine(width = 50)
    @Comment("操作用户的IP地址")
    private String remoteAddr;

    @Column(hump = true)
    @ColDefine(width = 200)
    @Comment("操作的URI")
    private String requestUri;

    @Column
    @ColDefine(width = 50)
    @Comment("操作的方式")
    private String method;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("操作用户代理信息")
    private String userAgent;

    @Column
    @ColDefine(customType = "LONGTEXT")
    @Comment("操作提交的数据")
    private String params;

    @Column
    @ColDefine(type = ColType.TEXT)
    @Comment("异常信息")
    private String exception;

    @Column(hump = true)
    @Comment("创建者")
    private Integer createBy;

    @Column(hump = true)
    @ColDefine(type = ColType.DATETIME)
    @Comment("创建时间")
    private Date createDate;

    private String UserName;

}
