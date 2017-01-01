package com.demo.floatwindowdemo;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import static com.demo.floatwindowdemo.R.id.textView;

/**
 * Created by lyt on 2016/12/31.
 */

public class MyColor {

    void setColor(View view) {
        TextView tv = (TextView)view;
        SpannableStringBuilder builder = new SpannableStringBuilder(tv.getText().toString());

        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
        ForegroundColorSpan whiteSpan = new ForegroundColorSpan(Color.WHITE);
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.BLUE);
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(Color.GREEN);
        ForegroundColorSpan yellowSpan = new ForegroundColorSpan(Color.YELLOW);

        builder.setSpan(redSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(whiteSpan, 1, 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        builder.setSpan(blueSpan, 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(greenSpan, 3, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(yellowSpan, 4,5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
