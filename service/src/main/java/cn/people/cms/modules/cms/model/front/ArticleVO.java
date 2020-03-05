package cn.people.cms.modules.cms.model.front;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by lml on 2016/12/22.
 */

@Data
public class ArticleVO{

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date beginTime;//查询起始时间
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date endTime;//查询截止时间

    private Integer categoryId;
    private Integer delFlag;
    private String title;
    private String keywords;
    private String type;//文章类型
    private Boolean inSubject;
    private String sysCode;
    private String source;
    private Integer pageNumber;
    private Integer pageSize;
    private Integer userId;
    //降序检索字段
    private String desc;
    //升序检索字段
    private String asc;
    //操作人
    private String operateUser;
    private Integer apiType;
    private Integer importType;
    public ArticleVO(){
        inSubject=false;
    }

    private Integer siteId;

}
