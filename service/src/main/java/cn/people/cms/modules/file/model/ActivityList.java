package cn.people.cms.modules.file.model;

import lombok.Data;

import java.util.Date;

/**
 * User: 张新征
 * Date: 2017/3/6 16:36
 * Description:
 */
@Data
public class ActivityList {
    private String RunId;
    private String Name;
    private String Type;
    private String State;
    private String JobId;
    private Date StartTime;
    private Date EndTime;
}
