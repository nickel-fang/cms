package cn.people.cms.config;

import cn.people.cms.modules.user.model.Menu;
import cn.people.cms.modules.user.model.User;
import cn.people.cms.modules.user.service.IAuthService;
import cn.people.cms.modules.user.service.IUserService;
import cn.people.cms.modules.user.service.impl.MenuService;
import cn.people.domain.IUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.nutz.dao.Cnd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class MyAuthorizingRealm extends AuthorizingRealm{

    @Autowired
    private IAuthService authService;

    @Autowired
    private IUserService userService;

    @Autowired
    private MenuService menuService;

    @Value("${theone.project.code}")
    private String projectCode;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        IUser user = (IUser) principals.getPrimaryPrincipal();
        Integer userId = user.getId();

        List<String> permsList = new ArrayList<String>();


        User user1 = new User();
        user1.setId(userId);
        user1.setName(user.getName());

        List<Menu> menus;
        if(userId == 1){
            menus = menuService.query(null, Cnd.where("del_flag", "=", 0));
        }else {
            menus = userService.getUserMenus(user1);
        }
        if(null != menus && menus.size() > 0){
            for(Menu menu : menus){
                if (StringUtils.isBlank(menu.getPermission())){
                    continue;
                }
                permsList.add(menu.getPermission());
            }
        }

        //用户权限列表
        Set<String> permsSet = new HashSet<String>();
        for (String perms : permsList) {
            if (StringUtils.isBlank(perms)) {
                continue;
            }
            permsSet.addAll(Arrays.asList(perms.trim().split(",")));
        }

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);
        return info;
    }


    /**
     * 认证(登录时调用)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
        AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();
        String password = new String((char[]) token.getCredentials());

        IUser user = authService.login(username,password);

        if (user == null) {
            throw new IncorrectCredentialsException("账号或密码不正确");
        }

        User user1 = userService.fetch(username);
        if (user1 == null) {
            throw new IncorrectCredentialsException("联系管理员，刷新用户");
        }
        user.setId(user1.getId());
        // 把当前用户放入到session中
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession(true);
        session.setAttribute("CURRENT_USER", user);

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(user, password, getName());
        return info;
    }
}
