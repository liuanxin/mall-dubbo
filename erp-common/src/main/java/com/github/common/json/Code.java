package com.github.common.json;

/** 返回码 */
public enum Code {
    /** 失败 */
    FAIL(0),
    /** 成功 */
    SUCCESS(1),
    /** 未登录 */
    NO_LOGIN(10);

    int flag;
    Code(int flag) { this.flag = flag; }
    public int getFlag() { return flag; }
}
