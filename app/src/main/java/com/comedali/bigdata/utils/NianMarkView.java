package com.comedali.bigdata.utils;

import android.content.Context;
import android.widget.TextView;

import com.comedali.bigdata.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

/**
 * Created by 刘杨刚 on 2018/9/11.
 */
public class NianMarkView extends MarkerView {
    private TextView tv_content;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
    public NianMarkView(Context context, int layoutResource) {
        super(context, layoutResource);
        tv_content = (TextView) findViewById(R.id.tv_Content);
    }


    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int m = (int) e.getX();
        int renshu = (int) e.getY();
        String name = null;
        if (m == 1) {
            name = "1月";
        } else if (m == 2) {
            name = "2月";
        } else if (m == 3) {
            name = "3月";
        } else if (m == 4) {
            name = "4月";
        } else if (m == 5) {
            name = "5月";
        } else if (m == 6) {
            name = "6月";
        } else if (m == 7) {
            name = "7月";
        } else if (m == 8) {
            name = "8月";
        } else if (m == 9) {
            name = "9月";
        } else if (m == 10) {
            name = "10月";
        } else if (m == 11) {
            name = "11月";
        } else if (m == 12) {
            name = "12月";
        }
        tv_content.setText(name + "  人数:" + renshu);
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight() - 30);
    }
}