package cn.people.cms.modules.cms.front;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.cms.model.front.UserVO;
import cn.people.cms.modules.user.model.User;
import cn.people.cms.modules.user.service.impl.UserService;
import cn.people.cms.modules.util.IpUtil;
import cn.people.cms.util.base.ShiroUtils;
import cn.people.domain.IUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/front/auth")
public class FrontAuthController {

    @Value("${theone.frontIp.section}")
    private String ipSection ;

    @Autowired
    private  UserService userService ;

    @PostMapping("/login")
    public Result login(@RequestBody UserVO userVO){

        log.info("into method login:{}",userVO);

        if(Lang.isEmpty(userVO)){
            return Result.error("用户信息错误！");
        }

        Subject subject = ShiroUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userVO.getUsername(), userVO.getPassword());
        subject.login(token);

        IUser mUser = ShiroUtils.getUser();
        if(Lang.isEmpty(mUser)){
            return Result.error("用户登陆异常！");
        }
        return Result.success(mUser) ;
    }

    /**
     * 获取用户详情
     * @return
     */
    @GetMapping("/getUserInfo")
    public Result getUserInfo(){
        log.info("into method getUserInfo():{}");
        IUser user = ShiroUtils.getUser();
        if(Lang.isEmpty(user)){
            return Result.error("用户未登陆！");
        }
        return Result.success(user);
    }


    /**
     * 判断是否部内/部外用户
     * @return
     */
    @GetMapping("/getUserFlag")
    public Result getUserFlag(){

        log.info("into method getUserFlag():{}");

        IUser mUser = ShiroUtils.getUser();
        if(Lang.isEmpty(mUser)){
            return Result.error("用户未登录！");
        }
        User user = userService.fetch(mUser.getUsername());

        UserVO userVO_out = new UserVO();
        if(IpUtil.ipExistsInRange(user.getIp(),ipSection)){
            //IP段内
            userVO_out.setIsInsideFlag(1);
        }else{
            //IP段外
            userVO_out.setIsInsideFlag(2);
        }

        return Result.success(userVO_out);
    }


    @GetMapping("/logout")
    public Result logout(){
        log.info("into method logout():{}");
        ShiroUtils.logout();
        return Result.success();
    }

}
