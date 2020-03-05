package cn.people.cms.modules.block.model;

import cn.people.cms.entity.BaseEntity;
import lombok.Data;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

/**
 * Created by lml on 2018/4/17.
 */
@Table("cms_block_relation_input")
@Data
public class Input extends BaseEntity{

    @Column
    @Comment("描述")
    private String description;

    @Column
    @Comment("标题")
    private String title;

    @Column
    @ColDefine(width = 1000)
    @Comment("说明")
    private String info;

    private Integer info1;

    public Integer getInfo1() {

        try {
            return Integer.parseInt(getInfo());
        }catch (Exception e) {

        }
        return 0;
    }
}
