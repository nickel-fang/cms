package cn.people.cms.config;

import cn.people.cms.base.api.Result;
import cn.people.cms.util.base.ShiroUtils;
import cn.people.cms.util.json.JsonUtil;
import cn.people.domain.IUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 访问拦截器
 */
public class LoginCheckInterceptor implements HandlerInterceptor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * 在实际的handler被执行前被调用
     */
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse response, Object o) throws Exception {

        String token = httpServletRequest.getHeader("Authorization");
        if (!StringUtils.isBlank(token)) {

            IUser user = ShiroUtils.getUser();
            if (user == null) {


                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                PrintWriter out = null ;
                try{

                    out = response.getWriter();
                    out.append(JsonUtil.toJson(Result.error(-10000,"")));
                    return false;
                }
                catch (Exception e){
                    response.sendError(500);
                    return false;
                }

            }

        }
        return true;// 只有返回true才会继续向下执行，返回false取消当前请求
    }

    /**
     * 在handler被执行后被调用
     */
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    /**
     * 当request处理完成后被调用
     */
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}
