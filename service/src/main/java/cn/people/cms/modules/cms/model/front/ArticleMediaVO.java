package cn.people.cms.modules.cms.model.front;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * User: 张新征
 * Date: 2017/3/10 16:55
 * Description:
 */
@Data
public class ArticleMediaVO implements Serializable {
    private Integer id;
    private String index;//索引（图集的排序，预留将来可以插入在正文的不同位置）
    private String title;//名称
    private String type;//类型（图集、视频、音频）
    private Long times;//时长（仅音频、视频有）
    private String image;//图片（音频视频是封面，图集就是图片）
    private String description;//描述
    private Integer height;//图片高度
    private Integer width;//图片宽度
    private List<MediaResourceVO> resources;//媒体资源（仅音频视频有，不同编码对应的地址）
}
