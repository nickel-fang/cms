package cn.people.cms.modules.block.service;

import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.block.model.BlockRelation;
import cn.people.cms.modules.block.model.VO.BlockRelationVO;
import org.nutz.dao.QueryResult;

/**
 * Created by lml on 2018/4/11.
 */
public interface IBlockRelationService  extends IBaseService<BlockRelation> {
    void saveMenu( BlockRelationVO relationVO);
    void saveArticle( BlockRelationVO relationVO);
    void saveInput(BlockRelationVO relationVO);
    QueryResult info(Integer blockId, Integer categoryId, Integer pageNo, Integer pageSize);
    void changeStatus(Integer id);
    void setInfo(BlockRelation relation);
    void changeStatus(Integer id,Integer delFlag);
    void saveImage(BlockRelationVO relationVO);
    void saveRelation(BlockRelationVO relationVO);
}
