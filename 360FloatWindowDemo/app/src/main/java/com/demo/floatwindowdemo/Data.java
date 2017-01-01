package com.demo.floatwindowdemo;

/**
 * Created by lyt on 2016/12/31.
 */
public class Data {

    private int dataChange;
    private Object mParam;

    public Data() {
    }

    public int getDataChange() {
        return dataChange;
    }

    public void setDataChange(int dataChange) {
        this.dataChange = dataChange;
    }

    public void setParam(Object param) {
        mParam = param;
    }

    public Object getParam() {
        return mParam;
    }

}
