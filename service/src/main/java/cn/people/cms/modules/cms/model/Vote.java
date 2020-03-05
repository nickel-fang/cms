package cn.people.cms.modules.cms.model;

import cn.people.cms.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by lml on 17-3-15.
 */
@Table("cms_vote")
@Data
public class Vote extends BaseEntity {

    @Column(hump=true)
    private Integer articleId;

    @Column
    @ColDefine(width = 500)
    @Comment("标题")
    private String title;

    @Column
    @Comment("类型 单选 多选")
    @ColDefine(type = ColType.INT)
    private Integer type;

    @Column
    @Comment("结束时间")
    @ColDefine(type = ColType.DATETIME)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date date;

    @Many(field = "voteId")
    List<VoteOption> options;

    @Column(hump = true)
    @Comment("是否显示结果")
    @ColDefine(type = ColType.BOOLEAN)
    private Boolean isShowResult;

    public Vote(){
        if(this.getDelFlag() == null){
            this.setDelFlag(Vote.STATUS_ONLINE);
        }
    }

}
