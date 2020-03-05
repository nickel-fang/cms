package cn.people.cms.modules.block.service.impl;

import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.modules.block.model.Block;
import cn.people.cms.modules.block.service.IBlockService;
import cn.people.cms.modules.templates.model.Template;
import cn.people.cms.modules.templates.service.ITemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by lml on 2018/4/10.
 */
@Slf4j
@Service
@Transactional(readOnly = true,rollbackFor = Exception.class)
public class BlockService extends BaseService<Block> implements IBlockService {

    @Autowired
    private ITemplateService templateService;

    @Override
    public List<Block> getBlockListByTid(Integer templateId){
        if(null == templateId){
            return null;
        }
        Template template = templateService.fetch(templateId);
        if(null == template){
            return null;
        }
        List<Block>list = template.getBlocks();
        return list.stream().sorted(Comparator.comparing(Block::getSort)).collect(Collectors.toList());
    }

}
