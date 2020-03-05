package cn.people.cms.modules.file.service;


import cn.people.cms.base.service.IBaseService;
import cn.people.cms.modules.file.model.MediaInfo;

import java.util.List;

/**
* 媒体信息Service
* @author zxz
*/
public interface IMediaInfoService extends IBaseService<MediaInfo> {

    /**
     * 关键字检索
     */
    List<MediaInfo> keyword(String keyword);
}