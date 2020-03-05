package cn.people.cms.modules.sys.model;

import cn.people.cms.entity.BaseEntity;
import lombok.Data;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

/**
* 字典
* @author cuiyukun
*/
@Table("sys_dict")
@Data
public class Dict extends BaseEntity {

    @Column
    @ColDefine(width = 200)
    @Comment("数据值")
    private String value;	// 数据值

    @Column
    @ColDefine(width = 20)
    @Comment("标签名")
    private String label;	// 标签名

    @Column
    @ColDefine(width = 200)
    @Comment("类型")
    private String type;	// 类型

    @Column
    @ColDefine(width = 200)
    @Comment("标签名")
    private String description;// 描述

    /**
     * 文章中的常量字符
     */
    public static class Constant{
        public static final String ID = "id";
        public static final String TYPE = "type";
        public static final String DESCRIPTION= "description";
    }

}