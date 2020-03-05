package cn.people.cms.modules.fields.model;

import cn.people.cms.entity.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * 字段描述
 *
 * @author lml
 */
@Table("fields_field")
@Data
@NoArgsConstructor
public class Field extends BaseEntity implements Comparable<Field> {

    @Column
    @ColDefine(width = 200)
    @Comment("字段名称")
    private String name;

    @Column
    @ColDefine(width = 200)
    @Comment("字段别名")
    private String slug;

    @Column
    @ColDefine(width = 20)
    @Comment("字段类型")
    private String type;

    @Column
    @ColDefine(width = 2000)
    @Comment("字段选项信息")
    private String options;

    private Integer groupId;

    @ManyMany(relation = "t_field_field_group", from = "field_id", to = "field_group_id")
    private List<FieldGroup> fieldGroups;

    @Column
    @ColDefine(width = 200)
    @Comment("占位符")
    private String placeholder;

    @Column(hump = true)
    @ColDefine(width = 200)
    @Comment("默认值")
    private String defaultValue;

    @Column
    @ColDefine(width = 1000)
    @Comment("描述字段")
    private String description;

    @Column
    @Comment("效验信息")
    @ColDefine(width = 1000)
    private String validate;

    @Column
    @ColDefine(width = 200)
    @Comment("展示逻辑")
    private String logic;

    @Column(hump = true)
    @ColDefine(width = 200)
    @Comment("单一或者多个实例")
    private String simpleOrMore;

    @Column
    private Integer sort;

    @Column(hump = true)
    @ColDefine(width = 500)
    @Comment("组排序json")
    private String sortJson;

    @Column(hump = true)
    @Comment("是否允许搜索")
    private Boolean isAllowSearch;

    private String fieldGroupName;

    public static final String FIELD_GROUPS = "fieldGroups";

    @Override
    public int compareTo(Field o) {
        return o.getSort() - this.getSort();
    }
}