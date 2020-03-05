package cn.people.cms.modules.block.model.VO;

import cn.people.cms.modules.block.model.ArticleItem;
import cn.people.cms.modules.block.model.ImageItem;
import cn.people.cms.modules.block.model.Input;
import cn.people.cms.modules.block.model.MenuItem;
import cn.people.cms.modules.cms.model.Article;
import lombok.Data;

import java.util.List;

/**
 * Created by lml on 2018/4/18.
 */
@Data
public class BlockRelationVO {
    private Integer blockId;
    private Integer categoryId;
    private Input input;
    private List<Integer> ids;
    private List<Article> items;
    private ImageItem image;
    private ArticleItem item;
    private MenuItem menu;
    private String type;
    private Integer destCategoryId;
    private Integer sort;
    private Integer delFlag;
    private Integer id;
}
