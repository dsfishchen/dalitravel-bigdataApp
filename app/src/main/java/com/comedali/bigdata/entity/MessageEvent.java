package com.comedali.bigdata.entity;

/**
 * Created by 刘杨刚 on 2018/9/29.
 */
public class MessageEvent {
    private String message;
    public  MessageEvent(String message){
        this.message=message;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
