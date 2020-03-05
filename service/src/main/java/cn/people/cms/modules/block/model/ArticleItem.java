package cn.people.cms.modules.block.model;

import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.cms.model.Article;
import cn.people.cms.modules.cms.model.front.ArticleMediaVO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

import java.util.List;

/**
 * Created by lml on 2018/4/11.
 */
@Data
@Table("cms_block_article")
@NoArgsConstructor
public class ArticleItem extends BaseEntity {

    @Column(hump = true)
    @Comment("原文编号")
    private Integer articleId;

    @Column
    @ColDefine(width = 500,notNull = true)
    @Comment("标题")
    private String title;

    @Column
    @ColDefine(width = 500)
    @Comment("摘要")
    private String description;

    @Column
    @ColDefine(width = 1000)
    @Comment("说明")
    private String info;

    @Column
    @ColDefine(width = 500)
    @Comment("外部链接")
    private String link;

    @Column
    @ColDefine(width = 500)
    @Comment("类型")
    private String type;

    @Column
    @Comment("权重 权重越大越靠前")
    private Integer weight;

    @Column
    @Comment("静态页面URL")
    private String url;

    @Column(hump = true)
    @ColDefine(width = 3000)
    @Comment("缩略图片")
    private String imageUrl;

    private List<ArticleMediaVO> imageJson;
    private List<ArticleMediaVO> audioJson;
    private List<ArticleMediaVO> videoJson;

    public  ArticleItem(Article article){
        this.title = article.getTitle();
        this.articleId = article.getId();
        this.weight = article.getWeight();
        this.link = article.getLink();
        this.url = article.getUrl();
        this.type = article.getType();
        this.description = article.getDescription();
        this.imageUrl = article.getImageUrl();
    }

    public static final String BLOCK_RELATION_ID="blockRelationId";

}
