package com.demo.floatwindowdemo;

import android.graphics.drawable.Drawable;

/**
 * Created by lyt on 2016/12/31.
 */

public class PakageMod {

    public String pakageName;
    public String appName;
    public Drawable icon;

    public PakageMod() {
        super();
    }

    public PakageMod(String pakageName, String appName, Drawable icon) {
        super();
        this.pakageName = pakageName;
        this.appName = appName;
        this.icon = icon;
    }
}
