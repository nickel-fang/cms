package cn.people.cms.modules.file.model;

import lombok.Data;

/**
 * User: 张新征
 * Date: 2017/3/6 16:01
 * Description:
 */
@Data
public class MessageBody {
    private String RunId;
    private String Name;
    private String Type;
    private String State;
    private MediaWorkflowExecution MediaWorkflowExecution;
}
