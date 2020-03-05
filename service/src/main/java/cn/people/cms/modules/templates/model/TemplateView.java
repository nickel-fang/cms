package cn.people.cms.modules.templates.model;

import lombok.Data;

/**
 * Created by lml on 2018/3/23.
 */
@Data
public class TemplateView {
    private Object data;
    private String resourcePrefix;
    private String site;

    public TemplateView(Object obj, String resourcePrefix) {
        this.data = obj;
        this.resourcePrefix = resourcePrefix;
    }
}
