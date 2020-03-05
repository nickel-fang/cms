package cn.people.cms.modules.sys.model;

import cn.people.cms.entity.TreeEntity;
import cn.people.cms.modules.cms.model.Article;
import cn.people.cms.modules.cms.model.Site;
import cn.people.cms.modules.templates.model.Template;
import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

import java.util.List;

/**
 * Created by lml on 2016/12/22.
 */
@Table("sys_category")
@Data
public class Category extends TreeEntity<Category> {

    @Column
    @Comment("链接")
    private String href;    // 链接

    @Column(hump = true)
    @Comment("频道模型编号")
    private Integer modelId;//栏目模型编号

    @Column
    private Integer sort;

    @Column(hump = true)
    @Comment("缩略图片")
    private String imageUrl;

    @Column(hump = true)
    @Comment("频道文章详情模板编号")
    public Integer templateId;

    @One(field = "templateId")
    private Template template;

    @Column(hump = true)
    @Comment("频道页面模板编号")
    public Integer pageTemplateId;

    @One(field = "pageTemplateId")
    private Template pageTemplate;

    @Column(hump = true)
    @Comment("站点编号")
    private Integer siteId;

    @Column(hump = true)
    @Comment("是否显示在首行 0是顶部菜单 1是内部区块 2是全部")
    private Integer isBanner;

    @One(field = "siteId")
    private Site site;

    @Column(hump = true)
    @Comment("展示条数")
    private Integer cardSize = 10;

    @Column
    @Comment("静态页面")
    private String url;

    @Column(hump=true)
    @Comment("cms与BBS对应的板块列表")
    private String bbsId ;


    private List<Category> children;//子菜单
    private List<Article> articles;//文章
    private String officeName;//组织机构名称

    @Override
    public void init() {
        super.init();
        if(null == isBanner){
            isBanner = 0;
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean flag = obj instanceof Category;
        if (flag) {
            Category category = (Category) obj;
            return category.getId().equals(this.getId());
        } else {
            return false;
        }
    }

    private String model;//栏目模型
    public static final String TEMPLATE="template";
    public static final String PAGE_TEMPLATE="pageTemplate";
    public static final String BLOCKS="blocks";

}
