package cn.people.cms.modules.cms.model.front;

import cn.people.cms.modules.cms.model.Article;
import lombok.Data;

import java.util.List;

/**
 * Created by lml on 2017/4/5.
 */
@Data
public class SubjectArticleVO {
    List<Article> list;
    Integer categoryId;
}
