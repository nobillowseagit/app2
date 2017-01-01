package com.demo.floatwindowdemo;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by lyt on 2016/12/31.
 */

public abstract class DataWatcher implements Observer {
    @Override
    public void update(Observable observable, Object data) {
    }

}
