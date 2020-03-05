//package cn.people.cms.config;
//
//import lombok.Data;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//
///**
// * User: 张新征
// * Date: 2017/8/18 13:47
// * Description:UserDetails接口规定了用户几个必须的方法
// */
//@Data
//public class JwtUser implements UserDetails {
//    private Integer id;
//    private String username;
//    private String password;
//    private Collection<? extends GrantedAuthority> permissions;
//
//    //返回分配给用户的权限列表
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return this.permissions;
//    }
//    // 账户是否未过期
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//    // 账户是否未锁定
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//    // 密码是否未过期
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    // 账户是否激活
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//
//}
