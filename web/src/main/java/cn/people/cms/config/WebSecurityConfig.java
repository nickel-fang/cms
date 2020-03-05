//package cn.people.cms.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
///**
// * User: 张新征
// * Date: 2017/8/17 15:02
// * Description:
// */
//@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//
//    // Spring会自动寻找同样类型的具体类注入，这里就是JwtUserService了
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Autowired
//    public void configureAuthentication(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        authenticationManagerBuilder
//                // 设置UserDetailsService
//                .userDetailsService(this.userDetailsService)
//                // 使用BCrypt进行密码的hash
//                .passwordEncoder(passwordEncoder());
//    }
//    // 装载BCrypt密码编码器
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public JwtFilter jwtFilter() throws Exception {
//        return new JwtFilter();
//    }
//
//    @Override
//    protected void configure(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.headers().frameOptions().disable();
//        httpSecurity
//                // 由于使用的是JWT，我们这里不需要csrf
//                .csrf().disable()
//                // 基于token，所以不需要session
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
//                .authorizeRequests()
//                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                // 允许对于网站静态资源的无授权访问
//                .antMatchers(
//                        HttpMethod.GET,
//                    "/api/sys/category/manually/**",
//                        "/",
//                        "/*.html",
//                        "/favicon.ico",
//                        "/**/*.html",
//                        "/**/*.css",
//                        "/**/*.js",
//                        "/**/*.png",
//                        "/static/**",
//                       "/public/**"
//                ).permitAll()
//                // 对于获取token的rest api要允许匿名访问
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
//                // 除上面外的所有请求全部需要鉴权认证
//                .anyRequest().authenticated();
//        // 禁用缓存
//        httpSecurity.headers().cacheControl();
//        //添加JWT filter
//        httpSecurity.addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
//    }
//}
