package cn.people.cms.modules.cms.web;

import com.baidu.ueditor.ActionEnter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by cuiyukun on 2017/12/7.
 */
@Controller
@RequestMapping("/api/ueditor")
public class UEditorController {

    @RequestMapping
    String index() {
        return "index";
    }

    @RequestMapping(value="/action")
    public void controller(HttpServletRequest request,
                           HttpServletResponse response, String action) {

        // response.setContentType("application/json");
        String rootPath = request.getSession().getServletContext().getRealPath("/");
        response.setHeader("Content-Type", "text/html");
        try {
            request.setCharacterEncoding("utf-8");
            String exec = new ActionEnter(request, rootPath).exec();
            PrintWriter writer = response.getWriter();
            writer.write(exec);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
