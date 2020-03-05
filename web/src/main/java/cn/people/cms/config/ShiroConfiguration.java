package cn.people.cms.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Configuration
public class ShiroConfiguration {


    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }


    @Bean(name = "shiroRealm")
    @DependsOn("lifecycleBeanPostProcessor")
    public MyAuthorizingRealm shiroRealm() {
        MyAuthorizingRealm realm = new MyAuthorizingRealm();
//        realm.setCredentialsMatcher(hashedCredentialsMatcher());
        return realm;
    }

    @Bean(name = "ehCacheManager")
    @DependsOn("lifecycleBeanPostProcessor")
    public EhCacheManager ehCacheManager() {
        EhCacheManager ehCacheManager = new EhCacheManager();
        return ehCacheManager;
    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(shiroRealm());
        securityManager.setCacheManager(ehCacheManager());//用户授权/认证信息Cache, 采用EhCache 缓存
        return securityManager;
    }




    /**
     * ShiroFilterFactoryBean 处理拦截资源文件问题。
     * 注意：单独一个ShiroFilterFactoryBean配置是或报错的，以为在
     * 初始化ShiroFilterFactoryBean的时候需要注入：SecurityManager
     * <p/>
     * Filter Chain定义说明
     * 1、一个URL可以配置多个Filter，使用逗号分隔
     * 2、当设置多个过滤器时，全部验证通过，才视为通过
     * 3、部分过滤器可指定参数，如perms，roles
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        //拦截器.
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        filters.put("authc", new MyFormAuthenticationFilter());

        filterChainDefinitionMap.put("/public/**", "anon");
        filterChainDefinitionMap.put("/*.css", "anon");
        filterChainDefinitionMap.put("/index.html", "anon");
//      filterChainDefinitionMap.put("/api/**", "anon"); //暂时放开该接口不进行登录限制，后台权限配置完备之后再进行控制
        filterChainDefinitionMap.put("/api/users/user/current", "anon");//获取当前用户接口
        filterChainDefinitionMap.put("/api/redis/clear","anon");
        filterChainDefinitionMap.put("/api/init/**","anon");
        filterChainDefinitionMap.put("/api/clear","anon");
        filterChainDefinitionMap.put("/api/front","anon");
        filterChainDefinitionMap.put("/auth/captcha.jpg","anon");
        filterChainDefinitionMap.put("/auth/login","anon");
        filterChainDefinitionMap.put("/api/analysis/**","anon");
        filterChainDefinitionMap.put("/api/search/article/**","anon");
        filterChainDefinitionMap.put("/auth/logout", "anon");
        filterChainDefinitionMap.put("/api/sys/menu/tree", "anon");
        filterChainDefinitionMap.put("/api/cms/block/**", "anon");

        filterChainDefinitionMap.put("/api/sys/category/tree", "anon");
        filterChainDefinitionMap.put("/api/sys/menu/**", "anon");
        filterChainDefinitionMap.put("/api/sys/menu/**", "anon");
        filterChainDefinitionMap.put("/api/sys/menu/treeView", "anon");
        filterChainDefinitionMap.put("/api/users/**", "anon");
        filterChainDefinitionMap.put("/api/sys/dict/**", "anon");
        filterChainDefinitionMap.put("/api/ueditor/**", "anon");
        filterChainDefinitionMap.put("/api/search/article/**", "anon");
        filterChainDefinitionMap.put("/auth/**", "anon");
        filterChainDefinitionMap.put("/**", "authc");

        // 对于获取token的rest api要允许匿名访问
//                .antMatchers("/auth/**","/api/templates/**","/api/search/**").permitAll()
//                .antMatchers(HttpMethod.POST,"/api/upload/**","/api/ueditor/**").permitAll()
//                .antMatchers(HttpMethod.GET,
//                        "/api/sys/menu/tree",
//                        "/api/sys/menu/treeView",
//                        "/api/users/office/tree",
//                        "/api/users/office/treeView",
//                        "/api/sys/dict/**",
//                        "/api/ueditor/**",
//                        "/ueditor/**",
//                        "/api/users/user/currentUpdate",
//                        "/api/search/article/**",
//                        "/api/live/talk/**","/auth/**").permitAll()

        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/index");
        shiroFilterFactoryBean.setLoginUrl("/");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        shiroFilterFactoryBean.setFilters(filters);
        return shiroFilterFactoryBean;
    }


    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator daap = new DefaultAdvisorAutoProxyCreator();
        daap.setProxyTargetClass(true);
        return daap;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor aasa = new AuthorizationAttributeSourceAdvisor();
        aasa.setSecurityManager(securityManager);
        return aasa;
    }


}
