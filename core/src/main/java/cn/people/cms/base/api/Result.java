package cn.people.cms.base.api;

import lombok.Data;

/**
 * Api返回结果
 */
@Data
public class Result {

    public static final int DEFAULT_CODE_SUCCESS = 0;
    public static final int DEFAULT_CODE_ERROR = -1;//可以交互的错误，错误信息给用户展示
    public static final int EXCEPTION_CODE_ERROR = -2;//系统异常，给前端返回异常信息，前端不展现给用户，只提示系统错误请联系管理员
    public static final int NOT_LOGGED_IN_CODE_ERROR = -3;//没有登录
    public static final int PERMISSION_DENIED_CODE_ERROR = -4;//没有权限
    public static final String DEFAULT_MSG_SUCCESS = "ok";

    private int code;
    private String msg;
    private Object data;

    public Result() {
    }

    public Result(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result success() {
        return new Result(DEFAULT_CODE_SUCCESS, DEFAULT_MSG_SUCCESS, null);
    }

    public static Result success(Object data) {
        return new Result(DEFAULT_CODE_SUCCESS, DEFAULT_MSG_SUCCESS, data);
    }

    public static Result success(String msg, Object data) {
        return new Result(DEFAULT_CODE_SUCCESS, msg, data);
    }

    public static Result error(int code, String msg) {
        return new Result(code, msg, null);
    }

    public static Result error(String msg) {
        return new Result(DEFAULT_CODE_ERROR, msg, null);
    }
}
