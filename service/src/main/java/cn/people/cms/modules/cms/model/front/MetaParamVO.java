package cn.people.cms.modules.cms.model.front;

import lombok.Data;

/**
 * Created by lml on 2017/1/10.
 */
@Data
public class MetaParamVO {
    private String slug;
    private Integer articleId;
    private String fieldCode;
    private String fieldValue;
}
