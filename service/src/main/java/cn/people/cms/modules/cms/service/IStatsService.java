package cn.people.cms.modules.cms.service;

import cn.people.cms.modules.cms.model.front.StatsVO;

import java.util.List;
import java.util.Map;

/**
 * Created by maliwei.tall on 2017/4/11.
 */
public interface IStatsService {

        List<StatsVO> queryStats(Map<String, String> paramMap);
        List<StatsVO> queryCount(Map<String, String> paramMap);
}
