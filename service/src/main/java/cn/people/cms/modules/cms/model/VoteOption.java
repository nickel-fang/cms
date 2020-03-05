package cn.people.cms.modules.cms.model;

import cn.people.cms.entity.BaseEntity;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * Created by lml on 17-3-15.
 */
@Table("cms_vote_option")
@Data
public class VoteOption extends BaseEntity {

    @Column(hump = true)
    @Comment("投票编号")
    @ColDefine(type = ColType.INT)
    private Integer voteId;

    @Column
    @ColDefine(width = 500)
    @Comment("选项名称")
    private String title;

    @Column
    @Comment("点击数量")
    @ColDefine(type = ColType.INT)
    private Integer hits;

    public VoteOption(){
        if(this.getDelFlag() == null){
            this.setDelFlag(Vote.STATUS_ONLINE);
        }
        if(hits==null){
            hits=0;
        }
    }

}
