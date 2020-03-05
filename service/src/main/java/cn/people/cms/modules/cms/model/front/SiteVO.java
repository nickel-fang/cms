package cn.people.cms.modules.cms.model.front;

import lombok.Data;

/**
 * Created by lml on 2018/3/23.
 */
@Data
public class SiteVO {
    private String name; // 站点名称
    private Integer pageNumber =1;
    private Integer pageSize = 10;

}
