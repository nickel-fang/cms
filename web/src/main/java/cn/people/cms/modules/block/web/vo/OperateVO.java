package cn.people.cms.modules.block.web.vo;

import cn.people.cms.modules.block.model.BlockRelation;
import lombok.Data;

import java.util.List;

/**
 * Created by lml on 2018/4/18.
 */
@Data
public class OperateVO {
    private List<Integer> ids;
    private Integer categoryId;
    private Integer templateId;
    private Integer delFlag;
    private List<BlockRelation> list;
    private Integer id;
}
