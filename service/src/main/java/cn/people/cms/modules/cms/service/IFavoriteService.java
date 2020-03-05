package cn.people.cms.modules.cms.service;


import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.cms.model.Favorite;
import org.nutz.dao.QueryResult;

public interface IFavoriteService extends IBaseService<Favorite> {


    public QueryResult listPage(Integer userId, Integer pageNumber, Integer pageSize);

    public Favorite doFavorite(Favorite favorite);

    public Favorite getByArticleId(Favorite favorite);

}
