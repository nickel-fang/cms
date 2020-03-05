package cn.people.cms.modules.file.model;

import lombok.Data;

/**
 * User: 张新征
 * Date: 2017/3/6 16:35
 * Description:
 */
@Data
public class InputFile {
    private String Bucket;
    private String Location;
    private String Object;
}
