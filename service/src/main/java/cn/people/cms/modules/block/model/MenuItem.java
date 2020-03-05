package cn.people.cms.modules.block.model;

import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.cms.model.Article;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

import java.util.List;

/**
 * Created by lml on 2018/4/13.
 */
@Data
@NoArgsConstructor
@Table("cms_block_relation_menu")
public class MenuItem extends BaseEntity {
    @Column
    @Comment("频道编号")
    private Integer categoryId;
    @Column(hump = true)
    @Comment("是否自动导入")
    private Boolean isAutoImport;

    @Column
    @Comment("文章数量")
    private Integer count = 10;
    @Column
    @Comment("链接")
    private String url;
    @Column
    @Comment("名称")
    private String title;
    @Column
    @Comment("描述")
    private String description;
    @Column(hump = true)
    @Comment("原始名称")
    private String oriName;

    @Column
    @Comment("说明")
    @ColDefine(width = 1000)
    private String info;

    private List<MenuItem> children;

    private List<Article>items;

    public MenuItem(Integer categoryId){
        this.categoryId = categoryId;
    }
}
