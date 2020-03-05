package cn.people.cms.modules.cms.service.impl;


import cn.people.cms.base.service.impl.BaseService;

import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.cms.model.Like;
import cn.people.cms.modules.cms.service.ILikeService;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = false,rollbackFor = Exception.class)
public class LikeService extends BaseService<Like> implements ILikeService {

    @Autowired
    ArticleService articleService ;

    @Autowired
    Dao dao ;


    @Override
    public QueryResult listPage(Integer userId, Integer pageNumber, Integer pageSize) {
        Cnd cnd = Cnd.where("userId","=",userId);
        cnd.desc("id");
        return super.listPage(pageNumber,pageSize,cnd);
    }

    @Override
    @Transactional
    public Like likeOrUnLike(Like like_in) {

        Like like = dao.fetch(Like.class, Cnd.where("article_id", "=", like_in.getArticleId()).and("user_id", "=", like_in.getUserId()));
        //首次点赞
        if (Lang.isEmpty(like)) {
            doLike(like_in);
            like_in.setDelFlag(BaseEntity.STATUS_ONLINE);
            return super.insert(like_in);
        }
        //存在点赞记录
        else {

            String flag = "";
            //未点赞-->点赞
            if (BaseEntity.STATUS_DELETE == like_in.getDelFlag()) {
                //文章点赞数 +1
                flag = " + ";
                like.setDelFlag(BaseEntity.STATUS_ONLINE);
                super.updateIgnoreNull(like);
                //点赞-->未点赞
            } else if (BaseEntity.STATUS_ONLINE == like_in.getDelFlag()) {
                //文章点赞数 -1
                flag = " - ";
                like.setDelFlag(BaseEntity.STATUS_DELETE);
                super.updateIgnoreNull(like);
            }
            else {
                return null;
            }

                Sql sql = Sqls.create("update cms_article set likes = likes " + flag + " 1 where id = @id ");
                sql.params().set("id", like_in.getArticleId());
                dao.execute(sql);

            return like;
        }

    }

    @Override
    @Transactional
    public Like doLike(Like like) {

        Sql sql = Sqls.create("update cms_article set likes = likes + 1 where id = @id ");
        sql.params().set("id",like.getArticleId());

        if( null != dao.execute(sql)){
            return like ;
        }

        return null;
    }

    @Override
    public Like getOneLike(Integer id,Integer userId) {
        //根据文章ID 获取点赞信息
        Like like = dao.fetch(Like.class,Cnd.where("article_id","=",id).and("user_id","=",userId));
        return like;
    }

}