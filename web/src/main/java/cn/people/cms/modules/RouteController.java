package cn.people.cms.modules;

import cn.people.cms.util.base.UserUtil;
import cn.people.domain.IUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 功能模块页面的路由
 */
@Controller
public class RouteController {

    @Value("${theone.login.url}")
    private String loginUrl;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String root() {
        return "index";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "index";
    }

    @GetMapping("{value}.html")
    public String route(@PathVariable String value, HttpServletResponse response) {
        if (value.equals("user")) {
            IUser user = UserUtil.getUser();
            if (user == null) {
                return "index";
            } else {
                response.addHeader("Authorization", user.getToken());
                try {
                    response.sendRedirect(loginUrl+"sso.action?Authorization="+user.getToken());
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return value;
    }

}
