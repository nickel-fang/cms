package cn.people.cms.modules.file.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * User: 张新征
 * Date: 2017/3/6 16:36
 * Description:
 */
@Data
public class MediaWorkflowExecution {
    private String MediaWorkflowId;
    private String Name;
    private String RunId;
    private String MediaId;
    private Input Input;
    private String State;
    private List<ActivityList> ActivityList;
    private Date CreationTime;
}
