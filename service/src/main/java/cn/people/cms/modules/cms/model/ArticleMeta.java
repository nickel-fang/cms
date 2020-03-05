package cn.people.cms.modules.cms.model;

import cn.people.cms.entity.BaseEntity;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * 文章元数据
 */
@Table("cms_article_meta")
@TableIndexes({@Index(name = "INDEX_CMS_ARTICLE_META_ARTICLE_ID", fields = {"articleId"}, unique = false)})
@Data
public class ArticleMeta extends BaseEntity {

	public ArticleMeta(){
		this.setDelFlag(BaseEntity.STATUS_ONLINE);
	}

	@Column(hump=true)
	private Integer articleId;

	@One(field="articleId")
	private Article article;

	@Column(hump=true)
	@ColDefine(width = 200)
	@Comment("字段编号")
	private String fieldCode;

	@Column(hump=true)
	@ColDefine(width = 500)
	@Comment("字段值")
	private String fieldValue;

	@Column(hump=true)
	@Comment("字段组id")
	private Integer fieldGroupId;

	public static String FIELD_CODE = "field_code";
	public static String FIELD_GROUP_ID= "field_group_id";

}
