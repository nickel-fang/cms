package cn.people.cms.config;

import com.google.common.base.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

import static com.google.common.base.Predicates.or;
import static springfox.documentation.builders.PathSelectors.regex;

/**
 * Created by Cheng on 2017/2/9.
 */
@EnableSwagger2
@Profile("!release")
@Configuration
public class SwaggerConfig {
  @Value("${server.context-path}")
  private String contextPath;

  private ApiInfo initApiInfo() {
      Contact contact = new Contact("后台开发团队", "http://www.people.cn", "app@people.cn");

      ApiInfo apiInfo = new ApiInfo("cms项目 Platform API",//大标题
          initContextInfo(),//简单的描述
          "0.0.1-SNAPSHOT",//版本
          "服务条款",
          contact,
          "The Apache License, Version 2.0",//链接显示文字
          "http://www.people.cn", //网站链接
          new ArrayList()
      );

      return apiInfo;
  }

  private String initContextInfo() {
    StringBuffer sb = new StringBuffer();
    sb.append("基于REST API，用<strong>GET、POST、PATCH、DELETE</strong>分别代表基础的获取、全量修改、局部修改以及删除的方法。")
      .append("<br><strong>通用接口无法满足</strong>或者<strong>需要特别加权限控</strong>的，<strong>再进行自定义接口</strong>，如上下线、批量操作等。注重统一的接口规范。")
      .append("<br><strong>请注意接口规范！！！</strong>");

    return sb.toString();
  }

  @Bean
  public Docket restfulApi() {
    return new Docket(DocumentationType.SWAGGER_2)
      .groupName("RestfulApi")
      .genericModelSubstitutes(ResponseEntity.class)
      .useDefaultResponseMessages(true)
      .forCodeGeneration(false)
      .pathMapping(contextPath)
      .select()
      .paths(doFilteringRules())
      .build()
      .apiInfo(initApiInfo());
  }

  /**
   * 设置过滤规则
   * 这里的过滤规则支持正则匹配
   * @return
   */
  private Predicate<String> doFilteringRules() {
    return or(
      regex("/api.*")
    );
  }
}
