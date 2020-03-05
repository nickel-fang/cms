package cn.people.cms.modules.sys.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.modules.cms.model.Article;
import cn.people.cms.modules.cms.service.impl.ArticleService;
import cn.people.cms.modules.user.model.User;
import cn.people.cms.util.base.ShiroUtils;
import cn.people.cms.util.text.StringUtils;
import cn.people.domain.IUser;
import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 */
@RestController
@Slf4j
@RequestMapping("auth")
public class AuthController {
    @Value("${theone.project.code}")
    private String projectCode;

    @Autowired
    private ArticleService articleService;

    @GetMapping("current")
    public Result getCurrentUser() {
        IUser user = null;
        try {
            user = ShiroUtils.getUser();
        } catch (Exception e) {
            log.warn("用户未登陆");
        }
        if (!Lang.isEmpty(user)){
            if (!Lang.isEmpty(user.getUsername())) {
                user.setIRoleList(null);
                return Result.success(user);
            }
        }else {
            /*user = new IUser();
            user.setId(1);
            if(null != system){
                user.setISystem(system);
            }
            return Result.success(user);*/
            return Result.error("用户信息有误");
        }
        return Result.error("系统错误");
    }

    @PostMapping("saveArticle")
    public ResponseEntity<Void> saveArticle(@RequestBody Article article){
        if(!Lang.isEmpty(article)){
            articleService.save(article);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }




    @Autowired
    private Producer producer;

    @PostMapping(value = "/login")
    public Result login(@RequestBody User user,HttpServletRequest request) {
        try {


            String validateCode = (String)request.getSession().getAttribute("validateCode");


            if (StringUtils.isBlank(user.getValidateCode())) {
                return Result.error("验证码不能为空！");
            }


            if (StringUtils.isBlank(validateCode) || !user.getValidateCode().equals(validateCode)) {
                return Result.error("验证码错误！");
            }

            Subject subject = ShiroUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(user.getUsername(), user.getPassword());
            subject.login(token);

            IUser returnUser = ShiroUtils.getUser();


            return Result.success(returnUser);

        }catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }


    @RequestMapping("/captcha.jpg")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        //生成文字验证码
        String text = producer.createText();
        //生成图片验证码
        BufferedImage image = producer.createImage(text);
        request.getSession().setAttribute("validateCode",text);
        String validateCode = (String)request.getSession().getAttribute("validateCode");

        log.info("validateCode===" + validateCode);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
    }
}
