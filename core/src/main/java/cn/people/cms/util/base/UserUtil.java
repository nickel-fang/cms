package cn.people.cms.util.base;

import cn.people.api.RpcIMenuService;
//import cn.people.cms.config.JwtUser;
import cn.people.cms.util.mapper.BeanMapper;
import cn.people.domain.IUser;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

/**
 */
public class UserUtil {
    public static IUser getUser(){
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if(null == authentication || null == authentication.getPrincipal() || "anonymousUser".equalsIgnoreCase(authentication.getPrincipal().toString())){
//            return null;
//        }
//        JwtUser jwtUser = (JwtUser)authentication.getPrincipal();
//        if(null == jwtUser){
//            return null;
//        }
//        IUser user = BeanMapper.map(jwtUser, IUser.class);
//        List<String> permissions = jwtUser.getAuthorities().stream().map(permission -> permission.getAuthority()).collect(Collectors.toList());
//        if(null != permissions && permissions.size() > 0){
//            user.setPermissions(permissions);
//        }
        try {
            return ShiroUtils.getUser();
        }catch (Exception e) {
            return null;
        }

    }
}
