package cn.people.cms.modules.cms.model.type;

/**
 * Created by lml on 2018/5/24.
 */
public enum SourceType {

    MANUAL_IMPORT(1),
    TRS_IMPORT(0);

    private final Integer value;

    SourceType(Integer value) {
        this.value = value;
    }

    public Integer value() {
        return this.value;
    }
}
