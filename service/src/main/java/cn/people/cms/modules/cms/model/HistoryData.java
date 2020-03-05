package cn.people.cms.modules.cms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.*;

import java.util.Date;

/**
 * Created by lml on 2018/3/26.
 * 文章历史记录表
 */
@Data
@Table("cms_history_data")
@TableIndexes({@Index(name = "INDEX_CMS_HISTORY_DATA_ARTICLE_ID", fields = {"articleId"}, unique = false)})
@NoArgsConstructor
public class HistoryData{
    @Id
    @Comment("主键")
    @ColDefine(type = ColType.INT)
    private Integer id;
    @Column
    @Comment("文章编号")
    private Integer articleId;
    @Column
    @ColDefine(customType = "LONGTEXT")
    @Comment("内容")
    private String content;
    @Column
    @Comment("标题")
    private String title;
    @Column(hump = true)
    @Comment("列表标题")
    private String listTitle;
    @Column(hump = true)
    @ColDefine(width = 500)
    @Comment("肩标题")
    private String introTitle;
    @Column(hump = true)
    @ColDefine(width = 500)
    @Comment("副标题")
    private String subTitle;
    @Column
    @ColDefine(width = 500)
    @Comment("来源")
    private String source;
    @Column
    @ColDefine(width = 200)
    @Comment("作者")
    private String authors;
    @Column(hump = true)
    @ColDefine(notNull = true)
    @Comment("栏目ID")
    private Integer categoryId;
    private String categoryName;
    @Column(hump = true)
    @Comment("文章状态")
    private Integer delFlag;
    @Column(hump=true)
    @Comment("发布人")
    private String auditUser;
    @Column(hump=true)
    @Comment("发布人编号")
    private Integer auditBy;
    @Column(hump=true)
    @Comment("创建人")
    private String createUser;
    @Column(hump=true)
    @Comment("创建人编号")
    private Integer createBy;
    @Column(hump=true)
    @Comment("最后修改人")
    private String updateUser;
    @Column(hump=true)
    @Comment("最后修改人编号")
    private Integer updateBy;
    @Column(hump=true)
    @Comment("审核时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date auditAt;
    @Column(hump = true)
    @Comment("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createAt;
    @Column(hump = true)
    @Comment("最后修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateAt;

    public HistoryData(Article article){
        if(article!=null){
            this.articleId = article.getId();
            this.title = article.getTitle();
            this.listTitle = article.getListTitle();
            this.subTitle = article.getSubTitle();
            this.introTitle = article.getIntroTitle();
            this.source = article.getSource();
            this.categoryId = article.getCategoryId();
            this.delFlag = article.getDelFlag();
            this.auditUser=article.getAuditUser();
            this.auditBy = article.getAuditBy();
            this.auditAt = article.getAuditAt();
            this.createUser=article.getCreateUser();
            this.createBy = article.getCreateBy();
            this.createAt = article.getCreateAt();
            this.updateBy = article.getUpdateBy();
            this.updateUser = article.getUpdateUser();
            this.updateAt = article.getUpdateAt();
            if(article.getArticleData()!=null){
                ArticleData articleData = article.getArticleData();
                this.content = articleData.getContent();
            }
        }
    }

}
