package cn.people.cms.modules.cms.model.front;

import cn.people.cms.entity.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by lml on 2017/1/10.
 */
@Data
public class ArticleDetailVO extends BaseEntity implements Serializable {
    private Integer categoryId;  //分类编号
    private String authors;	//作者
    private String content; //内容
    private String copyfrom;	//来源
    private String description; //摘要
    private Integer id;	//文章编号
    private String image;	//图片
    private String keywords; //关键字
    private String link;	//文章链接
    private Date publishDate; //发布时间
    private String title; //标题
    private String relation;
    private List<Map<String,Object>> relationMap;//给前端返回相关链接id,title组成的map
    private String mediaType;//媒体类型
}
