package cn.people.cms.entity;

/**
 * Created by lml on 2017/1/3.
 */
public enum MessageType {
    ARTICLE("article"),
    LOG("log");

    private final String value;

    private MessageType(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
