package cn.people.cms.modules.cms.model.type;

/**
 * Created by lml on 17-3-1.
 */
public enum ArticleType {

    /**
     * 文章类型枚举类 除引用、专题、直播之外其他是正常的文章类型
     */

    VIDEO("video"),//视频
    AUDIO("audio"),//音频
    LINK("link"),//链接
    IMAGE("image"),//图片
    COMMON("common");//普通

    private final String value;

    ArticleType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
