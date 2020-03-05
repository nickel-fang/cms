package cn.people.cms.modules.cms.model.front;

import lombok.Data;

import java.io.Serializable;

/**
 * User: 张新征
 * Date: 2017/3/10 16:58
 * Description:
 */
@Data
public class MediaResourceVO implements Serializable{
    private String url;
    private String enctype;
    private Long size;
}
