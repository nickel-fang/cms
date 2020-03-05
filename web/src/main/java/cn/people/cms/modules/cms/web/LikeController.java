package cn.people.cms.modules.cms.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.cms.model.Like;
import cn.people.cms.modules.cms.model.front.LikeVO;
import cn.people.cms.modules.cms.service.ILikeService;
import cn.people.cms.modules.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/like")
@Slf4j
public class LikeController {

    @Autowired
    private ILikeService likeService;

    private  User user = new User(); //测试时使用,联调时改成实际用户Id

    //保存收藏
        @RequestMapping("/likeOrNot")
    public Result isLike(@RequestParam Integer articleId, @RequestParam String title,@RequestParam String url,@RequestParam Integer delFlag) {
        log.info("isLike :{}",title);
//        IUser user = UserUtil.getUser();
        user.setId(1);
        Like like = new Like();
        like.setArticleId(articleId);
        like.setTitle(title);
        like.setUrl(url);
        like.setDelFlag(delFlag); //0 默认为点赞状态 ，3未点赞
        like.setUserId(user.getId());
        like.setCreateBy(user.getId());
        //点赞数据不存在的情况下
        Like like_out = likeService.likeOrUnLike(like) ;
         if(!Lang.isEmpty(like_out)) {
            return Result.success(like_out) ;
         }else{
            return Result.error("点赞失败!");
        }
    }

    //获取收藏列表
    @RequestMapping("/getLikeList")
    public Result listLike(@RequestParam(value = "pageNumber",defaultValue = "1") Integer pageNumber,
                           @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize) {
//        IUser user = UserUtil.getUser();
        log.info("listLike:{}");
        user.setId(1);
        QueryResult list = likeService.listPage(user.getId(),pageNumber, pageSize);
        return  Result.success(list);
    }

    //根据文章ID 获取一条收藏
    @RequestMapping("/getOneLike")
    public Result getOneLike(@RequestParam Integer articleId) {
        log.info("getOneLike:{}", articleId);
        user.setId(1);
        Like like = likeService.getOneLike(articleId, user.getId());

        if (!Lang.isEmpty(like)) {
            return Result.success(like);
        } else {
            LikeVO likeVO = new LikeVO();
            likeVO.setArticleId(articleId);
            likeVO.setDelFlag(BaseEntity.STATUS_DELETE);
            return Result.success(likeVO);
        }
    }

}
