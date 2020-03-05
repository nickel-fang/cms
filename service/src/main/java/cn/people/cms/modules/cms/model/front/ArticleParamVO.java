package cn.people.cms.modules.cms.model.front;

import cn.people.cms.modules.cms.model.Article;
import lombok.Data;

import java.util.List;

/**
 * Created by lml on 2018/5/29.
 */
@Data
public class ArticleParamVO {
    private Article article;
    private List<Integer>list;
}
