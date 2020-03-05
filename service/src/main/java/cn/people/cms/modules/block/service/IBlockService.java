package cn.people.cms.modules.block.service;

import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.block.model.Block;

import java.util.List;

/**
 * Created by lml on 2018/4/10.
 */
public interface IBlockService extends IBaseService<Block> {
    List<Block> getBlockListByTid(Integer templateId);
}
