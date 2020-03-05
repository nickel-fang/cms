package cn.people.cms.modules.sys.model;

import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.fields.model.FieldGroup;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * Created by lml on 2017/4/28.
 */
@NoArgsConstructor
@Data
@Table("sys_category_model")
public class CategoryModel extends BaseEntity{
    @Comment("模型名称")
    @Column
    private String name;//模型名称

    @Comment("数据源类型")
    @Column(hump = true)
    private Integer sourceType;//数据源类型 1普通 2自定义
    @Comment("是否显示搜索栏")
    @Column(hump = true)
    private Integer searchBar;//是否显示搜索栏
    @Comment("表头项")
    @Column
    @ColDefine(width = 500)
    private String items;

    @Comment("操作项")
    @Column(hump = true)
    @ColDefine(width = 500)
    private String operateItems;

    @Column(hump=true)
    @ColDefine(width = 500)
    @Comment("类目列表类型配置")
    private String itemType;

    @Comment("详情页设置")
    @Column(hump = true)
    @ColDefine(width = 5000)
    private String detailItems;//详情页设置

    @ManyMany(relation = "t_category_model_field_group", from = "category_model_id", to = "field_group_id")
    List<FieldGroup> fieldGroups;//字段组 FIELDS

    public static final String FIELD_GROUPS = "fieldGroups";

}
