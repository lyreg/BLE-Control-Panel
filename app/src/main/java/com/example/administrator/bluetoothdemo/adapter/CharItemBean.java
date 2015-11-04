package com.example.administrator.bluetoothdemo.adapter;

/**
 * Created by Administrator on 2015/10/14.
 */
public class CharItemBean {

    private String uuid;
    private String value;
    private boolean isCheckedN;     //notify
    private boolean isCheckedI;     //indicate

    public CharItemBean(String uuid) {
        this.uuid = uuid;
        this.value = "";
        this.isCheckedN = false;
        this.isCheckedI = false;
    }

    public CharItemBean(String uuid, String value, boolean isCheckedN, boolean isCheckedI) {
        this.uuid = uuid;
        this.value = value;
        this.isCheckedN = isCheckedN;
        this.isCheckedI = isCheckedI;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setIsCheckedN(boolean isChecked) {
        this.isCheckedN = isChecked;
    }
    public void setIsCheckedI(boolean isChecked) {
        this.isCheckedI = isChecked;
    }

    public String getUuid() {
        return uuid;
    }

    public String getValue() {
        return value;
    }

    public boolean isCheckedN() {
        return isCheckedN;
    }

    public boolean isCheckedI() {
        return isCheckedI;
    }
}
