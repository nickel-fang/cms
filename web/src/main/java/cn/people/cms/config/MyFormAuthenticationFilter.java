package cn.people.cms.config;

import cn.people.cms.base.api.Result;
import cn.people.cms.util.base.ShiroUtils;
import cn.people.cms.util.json.JsonUtil;
import cn.people.domain.IUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;

@Slf4j
public class MyFormAuthenticationFilter extends FormAuthenticationFilter {

    @Override
    protected boolean onAccessDenied(ServletRequest request,
                                     ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String token = httpServletRequest.getHeader("Authorization");
        if (StringUtils.isNotBlank(token)) {
            IUser user = ShiroUtils.getUser();
            if (user == null) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json; charset=utf-8");
                PrintWriter out = null;
                try {
                    out = response.getWriter();
                    out.append(JsonUtil.toJson(Result.error(-10000, "")));
                    return false;
                } catch (Exception e) {
                    return false;
                }

            }
        }



        return true;
    }
}
