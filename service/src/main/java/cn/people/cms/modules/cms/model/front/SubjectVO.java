package cn.people.cms.modules.cms.model.front;

import cn.people.cms.modules.cms.model.Article;
import cn.people.domain.BaseModel;
import lombok.Data;

/**
 * Created by lml on 17-3-3.
 */
@Data
public class SubjectVO extends BaseModel {

    private Article article;
    private Boolean showTitle;//显示标题
    private String image;//图片
    private String title;//专题标题
    private Integer id;
    private String compile;//图书编著
    private String publicationTime;//发行时间
}
