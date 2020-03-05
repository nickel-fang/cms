package cn.people.cms.modules.file.model;

import cn.people.cms.entity.BaseEntity;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.Date;

/**
* 媒体信息
* @author zxz
*/
@Table("media_info")
@Data
public class MediaInfo extends BaseEntity {

    @Column
    @ColDefine(width = 200)
    @Comment("文件原名称")
    private String name;

    @Column
    @ColDefine(width = 200)
    @Comment("关键字 用于检索")
    private String keyword;

    @Name
    @Column
    @ColDefine(width = 255)
    @Comment("文件保存的名称")
    private String autoName;

    @Column(hump = true)
    @Comment("文件上传时间")
    private Date uploadTime;

    @Column(hump = true)
    @Comment("文件转码成功时间")
    private Date transTime;

    @Column
    @ColDefine(type = ColType.INT, width = 1)
    @Comment("文件所处状态 0上传成功 1转码成功")
    private Integer status;

    @Column
    @ColDefine(width = 200)
    @Comment("文件原路径")
    private String fileUrl;

    @Column
    @ColDefine(width = 200)
    @Comment("文件类型 audio音频 video视频")
    private String type;

    @Column
    @Comment("文件大小")
    private Long size;

    @Column(hump = true)
    @ColDefine(width = 50)
    @Comment("原文件码率(Kpbs)")
    private String bitRate;

    @Column(hump = true)
    @ColDefine(width = 500)
    @Comment("流畅url")
    private String ldUrl;

    @Column(hump = true)
    @Comment("流畅size")
    private Long ldSize;

    @Column(hump = true)
    @ColDefine(width = 50)
    @Comment("流畅码率(Kbps)")
    private String ldBitRate;

    @Column(hump = true)
    @ColDefine(width = 500)
    @Comment("标清url")
    private String sdUrl;

    @Column(hump = true)
    @Comment("标清大小")
    private Long sdSize;

    @Column(hump = true)
    @ColDefine(width = 50)
    @Comment("标清码率(Kbps)")
    private String sdBitRate;

    @Column(hump = true)
    @ColDefine(width = 500)
    @Comment("高清url")
    private String hdUrl;

    @Column(hump = true)
    @Comment("高清大小")
    private Long hdSize;

    @Column(hump = true)
    @ColDefine(width = 50)
    @Comment("高清码率(Kbps)")
    private String hdBitRate;

    @Column(hump = true)
    @ColDefine(width = 500)
    @Comment("mp3-128url")
    private String mp3BigUrl;

    @Column(hump = true)
    @Comment("mp3-128size")
    private Long mp3BigSize;

    @Column(hump = true)
    @ColDefine(width = 50)
    @Comment("mp3-128码率(Kpbs)")
    private String mp3BigBitRate;

    @Column(hump = true)
    @ColDefine(width = 500)
    @Comment("mp3-64url")
    private String mp3SmallUrl;

    @Column(hump = true)
    @Comment("mp3-64size")
    private Long mp3SmallSize;

    @Column(hump = true)
    @ColDefine(width = 50)
    @Comment("mp3-64size码率(Kpbs)")
    private String mp3SmallBitRate;

    @Column
    @ColDefine(width = 500)
    @Comment("视频文件截图")
    private String cover;

    @Column
    @ColDefine(width = 200)
    @Comment("阿里云媒体id")
    private String mediaId;

    @Column
    @Comment("媒体文件的时长 单位秒")
    private Long duration;

}