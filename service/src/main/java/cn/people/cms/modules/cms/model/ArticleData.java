package cn.people.cms.modules.cms.model;

import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.cms.model.front.ArticleMediaVO;
import cn.people.cms.util.base.annotation.NotNull;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 文章大字段内容
 */
@Table("cms_article_data")
@Data
public class ArticleData extends BaseEntity {
	@Id(auto = false)
	private Integer id;

	@Column
	@ColDefine(customType = "LONGTEXT")
	@NotNull
	@Comment("内容")
	private String content;

	@Column
	@ColDefine(type = ColType.TEXT)
	@Comment("图集")
	private String images;

	@Column
	@ColDefine(type = ColType.TEXT)
	@Comment("音频")
	private String audios;

	@Column
	@ColDefine(type = ColType.TEXT)
	@Comment("视频")
	private String videos;

	@Column(hump = true)
	@ColDefine(width = 500)
	@Comment("调查标题")
	private String surveyTitle;

	@Column(hump = true)
	@Comment("是否多选")
	private Boolean isMultipleChoice;

	@Column(hump = true)
	@Comment("是否显示调查结果")
	private Boolean isShowResult;

	@Column(hump = true)
	@Comment("调查截止日期")
	private Date endTime;

	@Column(hump = true)
	@Comment("内容json串")
	@ColDefine(customType = "LONGTEXT")
	private String contentJson;

	@Column
	@ColDefine(width = 500)
	@Comment("文件")
	private String files;

	@Column(hump=true)
	@ColDefine(width = 500)
	@Comment("相关新闻")
	private String relatedArticles;

	private List<ArticleMediaVO> imageJson;
	private List<ArticleMediaVO> audioJson;
	private List<ArticleMediaVO> videoJson;

}
