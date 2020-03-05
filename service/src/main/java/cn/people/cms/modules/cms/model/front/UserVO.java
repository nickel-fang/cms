package cn.people.cms.modules.cms.model.front;

import cn.people.cms.modules.sys.model.Category;
import cn.people.cms.modules.user.model.User;
import cn.people.domain.IUser;
import lombok.Data;
import org.nutz.dao.entity.annotation.Comment;

import java.util.List;

@Data
public class UserVO {

    @Comment("用户id")
    private Integer userid;

    @Comment("登录名")
    private String username;

    @Comment("姓名")
    private String name;

    private String password;

    @Comment("系统编码")
    private String sysCode ;

    @Comment("ip")
    private String ip;

    @Comment("备注")
    private String remark;

    @Comment("用户登陆Token")
    private String token;

    @Comment("用户登陆信息")
    private IUser iUser ;

    @Comment("用户信息")
    private User user ;

    @Comment("判断是否是部内或部外用户 1：部内 2：部外")
    private Integer isInsideFlag ;


}
