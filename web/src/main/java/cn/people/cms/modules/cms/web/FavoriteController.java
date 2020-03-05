package cn.people.cms.modules.cms.web;

import cn.people.cms.base.api.Result;
import cn.people.cms.entity.BaseEntity;
import cn.people.cms.modules.cms.model.Favorite;
import cn.people.cms.modules.cms.model.front.FavoriteVO;
import cn.people.cms.modules.cms.service.IFavoriteService;
import cn.people.cms.modules.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/users/favorite")
@Slf4j
public class FavoriteController {

    @Autowired
    private IFavoriteService favoriteService;

    private  User user = new User(); //测试时使用

    //保存收藏
    @RequestMapping("/favoriteOrNot")
    public Result addFavorite(@RequestParam Integer articleId, @RequestParam String title,@RequestParam String url,@RequestParam Integer delFlag) {

        log.info("favoriteOrNot :{}", articleId);
//        IUser user = UserUtil.getUser();
        user.setId(1); //为了方便测试

        Favorite favorite = new Favorite();
        favorite.setArticleId(articleId);
        favorite.setTitle(title);
        favorite.setUrl(url);
        favorite.setDelFlag(delFlag);  //0 收藏状态
        favorite.setUserId(user.getId());
        favorite.setCreateBy(user.getId());

        //没有收藏的情况下
        Favorite favorite_out = favoriteService.doFavorite(favorite) ;
        if(!Lang.isEmpty(favorite_out)){
            return Result.success(favorite_out);
        }else{
            return Result.error("收藏失败！");
        }
    }

    //获取收藏列表
    @RequestMapping("/getFavoriteList")
    public Result listFavorite(@RequestParam(value = "pageNumber",defaultValue = "1") Integer pageNumber,
                               @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize) {
//        IUser user = UserUtil.getUser();
        log.info("getFavoriteList:{}");
        user.setId(1);
        QueryResult list = favoriteService.listPage(user.getId(),pageNumber, pageSize);
        return  Result.success(list);
    }

    //根据ID 获取一条收藏
    @RequestMapping("/getOneFavorite")
    public Result getOneFavorite(HttpServletResponse response, HttpServletRequest request, @RequestParam Integer articleId) {
        response.setHeader("Access-Control-Allow-Credentials","*");

        log.info("getOneFavorite:{}",articleId);
        user.setId(1);
        Favorite favorite = new Favorite();
        favorite.setArticleId(articleId);
        favorite.setUserId(user.getId());

        Favorite favorite_out = favoriteService.getByArticleId(favorite) ;
        if(!Lang.isEmpty(favorite_out)) {
            return Result.success(favorite_out);
        }else{
            FavoriteVO favoriteVO = new FavoriteVO();
            favoriteVO.setDelFlag(BaseEntity.STATUS_DELETE);
            favoriteVO.setArticleId(articleId);
            return Result.success(favoriteVO);
        }
    }
}
