package cn.people.cms.modules.cms.service.impl;


import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.cms.service.IFavoriteService;
import cn.people.cms.modules.cms.model.Favorite;
import jdk.nashorn.internal.objects.annotations.Where;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = false,rollbackFor = Exception.class)
public class FavoriteService extends BaseService<Favorite> implements IFavoriteService {

    @Autowired
    Dao dao ;

    @Override
    public QueryResult listPage(Integer userId, Integer pageNumber, Integer pageSize) {
        Cnd cnd = Cnd.where("userId","=",userId);
        cnd.desc("id");
        return super.listPage(pageNumber,pageSize,cnd);
    }

    @Override
    public Favorite doFavorite(Favorite inFavorite) {
        Favorite favorite = dao.fetch(Favorite.class,Cnd.where("article_id","=",inFavorite.getArticleId()).and("user_id","=",inFavorite.getUserId()));
        //如果没有收藏记录，则保存
        if(Lang.isEmpty(favorite)){
              inFavorite.setDelFlag(BaseEntity.STATUS_ONLINE);
              return super.insert(inFavorite);
        }else{
            //收藏-->未收藏
            if(BaseEntity.STATUS_ONLINE == inFavorite.getDelFlag()){
                favorite.setDelFlag(BaseEntity.STATUS_DELETE);
                if(super.updateIgnoreNull(favorite) > 0 ){
                    return favorite ;
                }
            }
            //未收藏--》收藏
            else if(BaseEntity.STATUS_DELETE == inFavorite.getDelFlag()){
                favorite.setDelFlag(BaseEntity.STATUS_ONLINE);
                if(super.updateIgnoreNull(favorite) > 0 ){
                    return favorite ;
                }
            }
        }
        return null ;
    }

    @Override
    public Favorite getByArticleId(Favorite inFavorite) {

        Favorite favorite = dao.fetch(Favorite.class,Cnd.where("article_id","=",inFavorite.getArticleId()).and("user_id","=",inFavorite.getUserId())) ;

        return favorite;
    }
}
