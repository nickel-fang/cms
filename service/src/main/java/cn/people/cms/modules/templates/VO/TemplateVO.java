package cn.people.cms.modules.templates.VO;

import lombok.Data;

/**
 * Created by lml on 2018/1/22.
 */
@Data
public class TemplateVO {
    private Integer pageNumber;
    private Integer pageSize;
    private String name;
    private Integer siteId;
    private String type;
    private String tag;
}
