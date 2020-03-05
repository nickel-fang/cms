package cn.people.cms.modules.cms.model;

import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.cms.model.front.ArticleMediaVO;
import cn.people.cms.modules.cms.model.front.MediaResourceVO;
import cn.people.cms.modules.cms.model.type.SysCodeType;
import cn.people.cms.modules.fields.model.FieldGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 文章
 */
@Table("cms_article")
@Data
@TableIndexes({@Index(name = "INDEX_CMS_ARTICLE_SEARCH", fields = {"categoryId"}, unique = false)})
public class Article extends BaseEntity {

	@Column(hump = true)
	@ColDefine(width = 20, notNull = true)
	@Comment("系统编码 引用文章可能来自其他系统模块，系统编码用于区分原文所在系统模块")
	private String sysCode;

	@Column(hump = true)
	@Comment("文章ID 单独定义文章ID，而不是使用主键ID，可以兼顾实体文章与引用文章,与主键ID相同的时候是实体文章，与主键文章不同的时候是引用文章的ID")
	private Integer articleId;

    @Column
    @ColDefine(width = 20)
    @Comment("文章类型 普通新闻，图片，音频，视频，快讯，直播，专题")
    private String type;

	@Column
	@Comment("推荐")
	private Integer recommendation;

	@Column(hump = true)
    @ColDefine(width = 200)
	@Comment("是否引用")
	private Boolean isReference;

	@Column(hump = true)
	//@ColDefine(notNull = true)
	@Comment("栏目ID")
	private Integer categoryId;

	@Column(hump = true)
	@Comment("导入类型")
	private Integer importType;

	@Column
	@ColDefine(width = 500,notNull = true)
	@Comment("标题")
	private String title;

	@Column(hump = true)
	@ColDefine(width = 500,notNull = true)
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
	@Comment("外部链接")
	private String link;

	@Column
	@Comment("静态页面URL")
	private String url;

	@Column
	@ColDefine(width = 500)
	@Comment("来源")
	private String source;

	@Column(hump = true)
	@ColDefine(width = 500)
	@Comment("拒绝原因")
	private String rejectReason;

	@Column
	@ColDefine(width = 3000)
	@Comment("缩略图片")
	private String imageUrl;

	@Column
	@ColDefine(width = 1000)
	@Comment("摘要")
	private String description;

	@Column
	@Comment("权重 权重越大越靠前")
	private Integer weight;

	@Column
	@Default("0")
	@Comment("点赞数")
	private Integer likes;

	@Column
	@Default("0")
	@Comment("点击数")
	private Integer hits;

	@Column
	@Default("0")
	@Comment("评论数")
	private Integer comments;

	@Column
	@ColDefine(width = 200)
	@Comment("关键字")
	private String keywords;

	@Column
	@ColDefine(width = 200)
	@Comment("标签")
	private String tags;

	@Column
	@Comment("是否置顶")
	private Integer stick;

	@Column(hump = true)
	@Comment("稿源库文章编号")
	private Integer sourceId;

	@Column(hump=true)
    @ColDefine(type = ColType.DATETIME)
	@Comment("发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	private Date publishDate;

	@One(field = "id")
	public ArticleData articleData;

	@Many(field = "articleId")
	private List<ArticleMeta> metas;

	@Many(field = "articleId")
	@Comment("调查")
	private List<Vote> votes;

	@Column(hump=true)
	@Comment("责任人")
	private String responsibleUser;
	@Column
	@Comment("作者")
	private String authors;

	@Column(hump=true)
	@Comment("审核人编号")
	private Integer auditBy;
	@Column(hump=true)
	@Comment("审核时间")
	private Date auditAt;
	@Column(hump=true)
	@Comment("审核人姓名")
	private String auditUser;
	@Column(hump=true)
	@Comment("创建人姓名")
	private String createUser;
	@Column(hump=true)
	@Comment("修改人姓名")
	private String updateUser;

	@Column(hump=true)
    @Comment("是否允许同步论坛 1：允许  0:不允许")
    private String allowPost ;


	private Long date;//客户端的发布日期
	private List<Integer> mediaIds;//媒体信息表id
	private String audioUrl;//音频地址
	private String audioCover;//音频封面
	private String videoUrl;//视频地址
	private String videoCover;//视频封面
    private List<MediaResourceVO> medias;//客户端列表接口音频视频
    private List<ArticleMediaVO> detailMedias;//客户端详情接口音频视频
    private Long mediaTime;//音视频时长（秒）
	private String categoryName;
	private Integer imageNum;//图集类型图片张数
	private List<FieldGroup>fieldGroups;//字段组定义信息
	private Integer column;//专题下区块中的文章源栏目

	private List<ArticleMediaVO> imageJson;
	private List<ArticleMediaVO> audioJson;
	private List<ArticleMediaVO> videoJson;

	@Override
	public void init() {
		if(sysCode==null){
			sysCode = SysCodeType.ARTICLE.value();
		}
		if(getDelFlag()==null){
			setDelFlag(Article.STATUS_DRAFT);
		}
		if(hits==null){
			hits=0;
		}
		if(likes==null){
			likes=0;
		}
		if(comments==null){
			comments=0;
		}
		if(isReference==null){
			isReference=false;
		}
		if(stick ==null){
			stick =0;
		}
	}

	/**
	 * 文章中的常量字符
	 */
	public static class Constant{
		public static final String IN_SUBJECT = "in_subject";
        public static final String ID = "id";
		public static final String TITLE = "title";
		public static final String PUBLISH_DATE = "publish_date";
		public static final String CREATE_AT= "create_at";
		public static final String WEIGHT = "weight";
		public static final String CATEGORY_ID = "category_id";
        public static final String TYPE = "type";
		public static final String IS_REFERENCE="is_reference";
		public static final String MATAS = "metas";
		public static final String CREATE_USER = "createUser";
		public static final String AUDIT_USER = "auditUser";
		public static final String STICK = "stick";
		public static final String RECOMMENDATION = "recommendation";
	}

	//文章状态标识
	public static final int STATUS_ONLINE = 0;//上线
	public static final int STATUS_DRAFT = 1;//草稿
	public static final int STATUS_AUDIT = 2;//待审核

	public static final int STATUS_NO_AUDIT = 4;//待修改
	public static final int STATUS_SOURCE= 5;//稿源类型
	public static final int STATUS_OFFLINE = 6;//下线
	public static final int STATUS_DELETE = 3;//删除



	public static final int IMPORT_TYPE = 1; //trs

}
