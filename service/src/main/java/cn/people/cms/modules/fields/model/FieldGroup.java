package cn.people.cms.modules.fields.model;

import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.sys.model.CategoryModel;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * 字段组
 *
 * @author lml
 */
@Table("fields_field_group")
@Data
public class FieldGroup extends BaseEntity {

    @Column
    @ColDefine(width = 200)
    @Comment("字段组名称")
    private String name;

    @Column
    @ColDefine(width = 100)
    @Comment("字段组描述")
    private String description;

    @ManyMany(relation = "t_field_field_group", from = "field_group_id", to = "field_id")
    private List<Field> fields;

    @ManyMany(relation = "t_category_model_field_group", from = "field_group_id", to = "category_model_id")
    private List<CategoryModel> categoryModels;//字段组 FIELDS

    public static final String CATEGORY_MODELS = "categoryModels";
    public static final String FIELDS = "fields";

}