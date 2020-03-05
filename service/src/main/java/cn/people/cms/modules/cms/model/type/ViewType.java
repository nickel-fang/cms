package cn.people.cms.modules.cms.model.type;

/**
 * Created by lml on 2017/4/1.
 */
public enum ViewType {
    BANNER("banner"),//直播

    NORMAL("normal");//文章

    private final String value;

    ViewType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
