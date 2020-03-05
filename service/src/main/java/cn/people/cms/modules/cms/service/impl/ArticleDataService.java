package cn.people.cms.modules.cms.service.impl;

import cn.people.cms.base.service.impl.BaseService;
import cn.people.cms.modules.cms.model.ArticleData;
import cn.people.cms.modules.cms.model.front.ArticleMediaVO;
import cn.people.cms.modules.cms.service.IArticleDataService;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lml on 2016/12/23.
 */
@Service
@Transactional(readOnly = true)
public class ArticleDataService extends BaseService<ArticleData> implements IArticleDataService {

    @Override
    public ArticleData fetch(Integer id) {
        ArticleData data = super.fetch(id);
        if (data == null) {
            return null;
        }
        if (StringUtils.isNotBlank(data.getImages())) {
            data.setImageJson(JSONArray.parseArray(data.getImages(), ArticleMediaVO.class));
        }
        if (StringUtils.isNotBlank(data.getAudios())) {
            data.setAudioJson(JSONArray.parseArray(data.getAudios(), ArticleMediaVO.class));
        }
        if (StringUtils.isNotBlank(data.getVideos())) {
            data.setVideoJson(JSONArray.parseArray(data.getVideos(), ArticleMediaVO.class));
        }
        return data;
    }
}
