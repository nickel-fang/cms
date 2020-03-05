package cn.people.cms.modules.sys.service.impl;

import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.sys.model.Dict;
import cn.people.cms.modules.sys.service.IDictService;
import cn.people.cms.util.mapper.BeanMapper;
import cn.people.domain.IDict;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* 字典Service
* @author cuiyukun
*/
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class DictService extends BaseService<Dict> implements IDictService {

    /**
     * 添加一条字典记录
     *
     * @param dict
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object save(Dict dict)  {
        return super.save(dict);
    }

    /**
     * 伪删除一条字典
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int vDelete(Integer id) {
        return super.vDelete(id);
    }

    @Override
    public List<Dict> getListByType(String type) {
        return dao.query(tClass,getDelFlag(null).and(Dict.Constant.TYPE,"=",type));
    }

    @Override
    public QueryResult listPage(Integer pageNo, Integer pageSize, Dict dict){
        Cnd cnd = Cnd.NEW();
        if(StringUtils.isNotBlank(dict.getDescription())){
            cnd.and(Dict.Constant.DESCRIPTION,"like","%"+dict.getDescription().trim()+"%");
        }
        if(StringUtils.isNotBlank(dict.getType())){
            cnd.and(Dict.Constant.TYPE,"=",dict.getType());
        }
        cnd.and(BaseEntity.FIELD_STATUS, "<", BaseEntity.STATUS_DELETE).desc(Dict.Constant.ID);
        return listPage(pageNo,pageSize,cnd);
    }

    @Override
    public List getTypes() {
        Sql sql = Sqls.create("select distinct type from "+dao.getEntity(Dict.class).getTableName() +
                " where del_flag <3");
        return list(sql);
    }

    @Override
    public List<Dict> query(String value, String type) {
        Condition condition = Cnd.where("value", "=", value).and("type", "=", type);
        List<Dict> list = query(null, condition);
        return list;
    }
}