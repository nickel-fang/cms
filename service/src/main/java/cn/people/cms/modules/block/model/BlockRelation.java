package cn.people.cms.modules.block.model;

import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.block.model.VO.BlockRelationVO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * Created by lml on 2018/4/11.
 */
@Data
@Table("cms_block_relation")
@NoArgsConstructor
@TableIndexes({@Index(name = "INDEX_CMS_BLOCK_RELATION", fields = {"blockId","sourceId"}, unique = false)})
public class BlockRelation  extends BaseEntity{

    @Column(hump = true)
    @Comment("区块编号")
    private Integer blockId;

    @Column(hump = true)
    @Comment("对应频道编号")
    private Integer sourceId;

    @Column(hump = true)
    @Comment("绑定频道编号")
    private Integer destCategoryId;

    @Column(hump = true)
    @Comment("绑定频道文章数目")
    private Integer importNum;

    @Column(hump = true)
    @Comment("绑定文章编号")
    private Integer itemId;
    @Column(hump = true)
    @Comment("文章")
    @One(field = "itemId")
    private ArticleItem item;

    @Column(hump = true)
    @Comment("绑定图片编号")
    private Integer imageId;
    @Column(hump = true)
    @Comment("图片")
    @One(field = "imageId")
    private ImageItem image;

    @Column(hump = true)
    @Comment("绑定导航编号")
    private Integer menuId;
    @Comment("导航")
    @One(field = "menuId")
    private MenuItem menu;

    private List<Integer> ids;//导航ids

    @Column(hump = true)
    @Comment("绑定文本编号")
    private Integer inputId;
    @Comment("文本")
    @One(field = "inputId")
    private Input input;

    @Column
    @Comment("绑定频道展示条数")
    private Integer value = 10;

    @Column
    @Comment("描述")
    private String description;

    @Column
    @Comment("类型")
    private String type;

    @Column
    @Comment("排序")
    private Integer weight;

    public static final String BLOCK_ID="blockId";
    public static final String SOURCE_ID="sourceId";
    public static final String MENU="menu";
    public static final String ARTICLE="item";
    public static final String INPUT="input";
    public static final String IMAGE="image";
    public static final String ARTICLE_TYPE="article";
    public static final String WEIGHT="weight";

    @Override
    public void init() {
        if(this.getId() == null){
            return;
        }
        if(this.weight == null){
            setWeight(this.getId());
        }
        if(this.getDelFlag() == null){
            setDelFlag(BlockRelation.STATUS_OFFLINE);
        }
    }

    public BlockRelation(BlockRelationVO vo){
        this.blockId = vo.getBlockId();
        this.sourceId = vo.getCategoryId();
        this.type = vo.getType();
        this.setId(vo.getId());
        if(vo.getDelFlag() == null){
            setDelFlag(BlockRelation.STATUS_OFFLINE);
        }else {
            setDelFlag(vo.getDelFlag());
        }
    }
}
