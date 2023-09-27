package com.example.ttsinterface.entity;

/**
 * @author sodream
 * @date 2022/6/2 10:07
 * @content
 */
public class InterfaceResponse {

    /**
     * 返回响应
     */
    public Boolean success;

    /**
     * 响应内容
     */
    public String msg;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public InterfaceResponse(Boolean _success, String _msg) {
        this.msg = _msg;
        this.success = _success;
    }
}
