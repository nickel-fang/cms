package cn.people.cms.modules.cms.service;


import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.cms.model.Like;
import org.nutz.dao.QueryResult;

public interface ILikeService extends IBaseService<Like> {


    public QueryResult listPage(Integer userId, Integer pageNumber, Integer pageSize);

    public Like likeOrUnLike(Like like);

    public Like doLike(Like like);

    public Like getOneLike(Integer id,Integer userId);

}
