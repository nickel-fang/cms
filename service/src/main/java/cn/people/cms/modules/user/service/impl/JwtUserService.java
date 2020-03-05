//package cn.people.cms.base.service.impl;
//
//import cn.people.api.RpcIMenuService;
//import cn.people.api.RpcIUserService;
//import cn.people.cms.config.Authority;
//import cn.people.cms.config.JwtUser;
//import cn.people.cms.util.mapper.BeanMapper;
//import cn.people.domain.IMenu;
//import cn.people.domain.IUser;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * User: 张新征
// * Date: 2017/8/18 14:35
// * Description:
// */
//@Service
//public class JwtUserService implements UserDetailsService {
//    @Autowired
//    private RpcIUserService userService;
//    @Autowired
//    private MenuService menuService;
//    @Value("${theone.project.code}")
//    private String projectCode;
//
//    @Override
//    public JwtUser loadUserByUsername(String username) throws UsernameNotFoundException {
//        IUser user = userService.fetchUserByUsername(username);
//        if(null == user){
//            throw new UsernameNotFoundException(String.format("user not found with username '%s' .", username));
//        }
//        JwtUser jwtUser = BeanMapper.map(user, JwtUser.class);
//        List<Authority> list = new ArrayList<>();
//        List<IMenu> menus;
//        if(jwtUser.getId() == 1){
//            menus = menuService.query(projectCode);
//        }else {
//            menus = userService.getUserMenus(user);
//        }
//        if(null != menus && menus.size() > 0){
//            for(IMenu menu : menus){
//                if (StringUtils.isBlank(menu.getPermission())){
//                    continue;
//                }
//                Authority authority = new Authority(menu.getPermission());
//                list.add(authority);
//            }
//        }
//        jwtUser.setPermissions(list);
//        return jwtUser;
//    }
//}
