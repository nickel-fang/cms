package cn.people.cms.modules.block.model;

import cn.people.cms.entity.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

/**
 * Created by lml on 2018/4/23.
 */
@Data
@Table("cms_block_image")
@NoArgsConstructor
public class ImageItem extends BaseEntity {
    @Column
    @Comment("描述")
    private String description;

    @Column
    @Comment("标题")
    private String title;

    @Column
    @Comment("说明")
    private String info;
}
