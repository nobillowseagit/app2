package com.demo.floatwindowdemo;

import java.util.Observable;

/**
 * Created by lyt on 2016/12/31.
 */

public class DataChange extends Observable {

    private static DataChange instance = null;

    public static DataChange getInstance() {
        if (null == instance) {
            instance = new DataChange();
        }
        return instance;
    }

    public void notifyDataChange(Data data) {
        //被观察者怎么通知观察者数据有改变了呢？？这里的两个方法是关键。
        setChanged();
        notifyObservers(data);
    }

}